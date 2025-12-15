import java.io.ByteArrayInputStream
import java.net.URI
import java.util.UUID

import example.sensor_reading.SensorReading

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import com.datastax.oss.driver.api.core.config.DefaultDriverOption

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.expressions.Window

object SparkSensorPipeline extends App {

  println("🚀 Starting Pipeline B — Real-Time Stream Processor")

  // ------------------------------------------
  // CONFIG - Set these values before running
  // ------------------------------------------
  val kafkaBootstrap = "localhost:9092"
  val kafkaTopic     = "air_sensor_readings"

  // Cassandra (AWS Keyspaces) settings
  val cassKeyspace = "environment_grid"
  val cassTable    = "sensor_readings_by_zone"
  val cassContactPoint = "cassandra.us-east-1.amazonaws.com"
  val cassPort = 9142
  val cassRegion = "us-east-1"
  val cassUser = "USERNAME"     // your AWS Keyspaces username (access key)
  val cassPass = "PASSWORD"  // your AWS Keyspaces password (secret key)

  // S3 settings
  val awsS3Region   = "us-east-1"
  val s3AccessKey   = "ACCESSKEY"
  val s3SecretKey   = "SECRETKEY"
  val s3Bucket      = "smart-env-monitering-bucket"   // EXACT bucket name
  val s3Prefix      = "sensor-history/"               // folder/prefix within bucket

  // MySQL (thresholds)
  val jdbcUrl = "jdbc:mysql://DB_URL:3306/Smart_Environmental_MonitoringDB"
  val jdbcUser = "admin"
  val jdbcPassword = "PASSWORD"
  val mysqlTable = "pollution_threshold"

  // ------------------------------------------
  // Spark Session - keep S3A fields minimal (we use AWS SDK for uploads)
  // ------------------------------------------
  val spark = SparkSession.builder()
    .appName("PipelineB-SensorProcessor")
    .master("local[*]")
    .config("spark.kafka.bootstrap.servers", kafkaBootstrap)
    // keep s3a impl in case other parts need it
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .getOrCreate()

  import spark.implicits._

  // --------------------------
  // Schema + Kafka stream
  // --------------------------
  val schema = new StructType()
    .add("sensor_id", StringType)
    .add("zone_id", IntegerType)
    .add("pm2_5", DoubleType)
    .add("pm10", DoubleType)
    .add("co2_ppm", DoubleType)
    .add("device_status", StringType)
    .add("timestamp", LongType)

  val kafkaDF = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", kafkaBootstrap)
    .option("subscribe", kafkaTopic)
    .option("startingOffsets", "latest")
    .load()

  val jsonDF = kafkaDF.selectExpr("CAST(value AS STRING) AS json")

  val parsedDF = jsonDF
    .withColumn("data", from_json($"json", schema))
    .select("data.*")
    .filter(col("sensor_id").isNotNull && col("zone_id").isNotNull)

  val enrichedDF = parsedDF.withColumn("processing_time", current_timestamp())

  // --------------------------
  // Read thresholds from MySQL (one-time read at start)
  // --------------------------
  val jdbcProps = new java.util.Properties()
  jdbcProps.setProperty("user", jdbcUser)
  jdbcProps.setProperty("password", jdbcPassword)
  jdbcProps.setProperty("driver", "com.mysql.cj.jdbc.Driver")

  val thresholdDF = try {
    spark.read.jdbc(jdbcUrl, mysqlTable, jdbcProps)
      .withColumnRenamed("pm2_5_limit", "pm2_5_thresh")
      .withColumnRenamed("pm10_limit", "pm10_thresh")
      .withColumnRenamed("co2_limit", "co2_thresh")
  } catch {
    case e: Exception =>
      println(s"⚠️ Failed to read thresholds from MySQL: ${e.getMessage}")
      spark.emptyDataFrame
  }

  val finalDF = enrichedDF.join(thresholdDF, Seq("zone_id"), "left")
    .withColumn("is_anomaly",
      when(
        (col("pm2_5").isNotNull && col("pm2_5_thresh").isNotNull && col("pm2_5") > col("pm2_5_thresh"))
          .or(col("pm10").isNotNull && col("pm10_thresh").isNotNull && col("pm10") > col("pm10_thresh"))
          .or(col("co2_ppm").isNotNull && col("co2_thresh").isNotNull && col("co2_ppm") > col("co2_thresh")),
        lit(true)
      ).otherwise(lit(false))
    )
    .withColumn("ingestion_timestamp", (unix_timestamp() * 1000).cast("long"))

  val preparedDF = finalDF
    .withColumn("pm2_5", col("pm2_5").cast("float"))
    .withColumn("pm10", col("pm10").cast("float"))
    .withColumn("co2_ppm", col("co2_ppm").cast("float"))

  // ---------------------------------------------------------------------
  // AWS Keyspaces session builder
  // ---------------------------------------------------------------------
  def buildAwsKeyspacesSession(): CqlSession = {
    // set request consistency to LOCAL_QUORUM (AWS Keyspaces requires this)
    val config = DriverConfigLoader.programmaticBuilder()
      .withString(DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, cassRegion)
      .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "LOCAL_QUORUM")
      .build()

    CqlSession.builder()
      .withConfigLoader(config)
      .addContactPoint(new java.net.InetSocketAddress(cassContactPoint, cassPort))
      .withLocalDatacenter(cassRegion)
      // AWS Keyspaces uses SigV4 for auth normally; but many setups accept the access/secret as username/password
      .withAuthCredentials(cassUser, cassPass)
      .build()
  }

  // ---------------------------------------------------------------------
  // Helper case class for row items to write to Cassandra
  // ---------------------------------------------------------------------
  case class ZoneRow(sensor_id: String, timestamp: Long, pm2_5: Float, pm10: Float, co2_ppm: Float, device_status: String)

  // ---------------------------------------------------------------------
  // Write top-50 per zone to AWS Keyspaces: read existing per-zone, merge, take top-50, delete partition, re-insert
  // ---------------------------------------------------------------------
  def writeTop50ToCassandra(batchDF: DataFrame, batchId: Long): Unit = {
    println(s"🔥 Cassandra batch $batchId start")

    // safe empty check
    if (batchDF.head(1).isEmpty) {
      println("⚠️ Empty batch - skipping Cassandra")
      return
    }

    // collect new rows grouped by zone (small per-batch assumption)
    val newRowsByZone: Map[Int, Array[ZoneRow]] = batchDF
      .select("zone_id", "sensor_id", "timestamp", "pm2_5", "pm10", "co2_ppm", "device_status")
      .na.drop(Seq("zone_id", "sensor_id", "timestamp"))
      .as[(Int, String, Long, Float, Float, Float, String)]
      .collect()
      .groupBy(_._1)
      .map { case (zoneId, arr) =>
        zoneId -> arr.map { case (_, sensorId, ts, p25, p10, co2, status) =>
          ZoneRow(sensorId, ts, Option(p25).getOrElse(0.0f), Option(p10).getOrElse(0.0f), Option(co2).getOrElse(0.0f), Option(status).getOrElse(""))
        }
      }

    if (newRowsByZone.isEmpty) {
      println("⚠️ No zones in this batch — nothing to do")
      return
    }

    val session = buildAwsKeyspacesSession()

    try {
      // prepared statements
      val selectStmt = session.prepare(s"SELECT sensor_id, timestamp, pm2_5, pm10, co2_ppm, device_status FROM $cassKeyspace.$cassTable WHERE zone_id = ?")
      val deleteStmt = session.prepare(s"DELETE FROM $cassKeyspace.$cassTable WHERE zone_id = ?")
      val insertStmt = session.prepare(
        s"""INSERT INTO $cassKeyspace.$cassTable
           (zone_id, timestamp, pm2_5, pm10, co2_ppm, device_status, sensor_id)
           VALUES (?, ?, ?, ?, ?, ?, ?)"""
      )

      newRowsByZone.foreach { case (zoneId, newRows) =>
        // Fetch existing rows for this zone
        val existingRowsIter = try {
          session.execute(selectStmt.bind(Int.box(zoneId))).iterator()
        } catch {
          case ex: Exception =>
            println(s"⚠️ Failed to select existing rows for zone $zoneId: ${ex.getMessage}")
            java.util.Collections.emptyIterator()
        }

        val existingRowsBuffer = scala.collection.mutable.ArrayBuffer.empty[ZoneRow]
        while (existingRowsIter.hasNext) {
          val r = existingRowsIter.next()
          val sensorId = if (r.getString("sensor_id") != null) r.getString("sensor_id") else ""
          val ts = if (r.getLong("timestamp") != null) r.getLong("timestamp") else 0L
          // driver returns Doubles/BigDecimal depending on mapping; be defensive
          val pm25 = try { r.getFloat("pm2_5") } catch { case _: Throwable => Option(r.getObject("pm2_5")).map(_.toString.toFloat).getOrElse(0.0f) }
          val pm10 = try { r.getFloat("pm10") } catch { case _: Throwable => Option(r.getObject("pm10")).map(_.toString.toFloat).getOrElse(0.0f) }
          val co2  = try { r.getFloat("co2_ppm") } catch { case _: Throwable => Option(r.getObject("co2_ppm")).map(_.toString.toFloat).getOrElse(0.0f) }
          val status = if (r.getString("device_status") != null) r.getString("device_status") else ""
          existingRowsBuffer += ZoneRow(sensorId, ts, pm25, pm10, co2, status)
        }

        // Merge existing + new rows, sort desc by timestamp, keep top 50
        val merged = (existingRowsBuffer ++ newRows).sortBy(-_.timestamp).take(50)

        // Delete existing partition for this zone, then insert merged rows
        try {
          session.execute(deleteStmt.bind(Int.box(zoneId)))
        } catch {
          case ex: Exception => println(s"⚠️ Failed to delete zone $zoneId before insert: ${ex.getMessage}")
        }

        // Insert each row individually (no batch)
        merged.foreach { r =>
          try {
            val bound = insertStmt.bind(
              Int.box(zoneId),
              Long.box(r.timestamp),
              java.lang.Float.valueOf(r.pm2_5),
              java.lang.Float.valueOf(r.pm10),
              java.lang.Float.valueOf(r.co2_ppm),
              r.device_status,
              r.sensor_id
            )
            session.execute(bound)
          } catch {
            case ex: Exception =>
              println(s"⚠️ Failed to insert for zone $zoneId row ts=${r.timestamp}: ${ex.getMessage}")
          }
        }
        println(s"✔ Zone $zoneId: wrote ${merged.size} rows (top-50)")
      }
    } finally {
      session.close()
    }
  }

  // ---------------------------------------------------------------------
  // S3 uploader using AWS SDK (explicit) - avoids S3A / Hadoop dependency issues
  // ---------------------------------------------------------------------
  def s3Client(): AmazonS3 = {
    val creds = new BasicAWSCredentials(s3AccessKey, s3SecretKey)
    AmazonS3ClientBuilder.standard()
      .withRegion(awsS3Region)
      .withCredentials(new AWSStaticCredentialsProvider(creds))
      .build()
  }

  def writeToS3(batchDF: DataFrame, batchId: Long): Unit = {
    println(s"📝 S3 batch $batchId start")

    if (batchDF.head(1).isEmpty) {
      println("⚠️ Empty batch - skipping S3")
      return
    }

    val s3 = s3Client()

    // Ensure prefix formatting
    val prefix = if (s3Prefix.endsWith("/")) s3Prefix else s3Prefix + "/"

    // collect rows (assuming moderate batch size)
    val rows = batchDF.collect()

    rows.foreach { row =>
      try {
        val readingId = UUID.randomUUID().toString
        val sensorId = Option(row.getAs[String]("sensor_id")).getOrElse("")
        val zoneId = Option(row.getAs[Int]("zone_id")).getOrElse(0)
        val pm25 = Option(row.getAs[Float]("pm2_5")).getOrElse(0.0f)
        val pm10 = Option(row.getAs[Float]("pm10")).getOrElse(0.0f)
        val co2  = Option(row.getAs[Float]("co2_ppm")).getOrElse(0.0f)
        val isAnom = Option(row.getAs[Boolean]("is_anomaly")).getOrElse(false)
        val ts = Option(row.getAs[Long]("timestamp")).getOrElse(0L)
        val ingestionTs = Option(row.getAs[Long]("ingestion_timestamp")).getOrElse(0L)
        val devStatus = Option(row.getAs[String]("device_status")).getOrElse("")

        val protoBytes = SensorReading(
          readingId = readingId,
          sensorId = sensorId,
          zoneId = zoneId,
          pm25 = pm25,
          pm10 = pm10,
          co2Ppm = co2,
          isAnomaly = isAnom,
          timestamp = ts,
          ingestionTimestamp = ingestionTs,
          deviceStatus = devStatus
        ).toByteArray

        val key = s"${prefix}reading-${UUID.randomUUID().toString}.pb"

        val metadata = new ObjectMetadata()
        metadata.setContentLength(protoBytes.length)
        metadata.setContentType("application/octet-stream")

        val bais = new ByteArrayInputStream(protoBytes)
        s3.putObject(s3Bucket, key, bais, metadata)
        bais.close()

        println(s"✔ Uploaded to s3://$s3Bucket/$key")
      } catch {
        case ex: Exception =>
          println(s"❌ S3 upload failed for a row: ${ex.getMessage}")
      }
    }
  }

  // ---------------------------------------------------------------------
  // Start streaming foreachBatch
  // ---------------------------------------------------------------------
  val query = preparedDF.writeStream
    .outputMode("append")
    .foreachBatch { (batch: DataFrame, id: Long) =>
      // process each batch defensively: errors in Cassandra or S3 should not crash the query
      try {
        writeTop50ToCassandra(batch, id)
      } catch {
        case ex: Exception => println(s"❌ Error in Cassandra step for batch $id: ${ex.getMessage}")
      }

      try {
        writeToS3(batch, id)
      } catch {
        case ex: Exception => println(s"❌ Error in S3 step for batch $id: ${ex.getMessage}")
      }
    }
    .option("checkpointLocation", "chk/pipeline_b")
    .trigger(Trigger.ProcessingTime("10 seconds"))
    .start()

  println("✅ Pipeline started...")
  spark.streams.awaitAnyTermination()
}
