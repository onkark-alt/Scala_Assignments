import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import java.net.URI
import java.time.LocalDate

import scala.util.Try

// ------------------------------
// 1. Spark-friendly case class
// ------------------------------
case class SensorReadingRow(
                             readingId: String,
                             sensorId: String,
                             zoneId: Int,
                             pm2_5: Float,
                             pm10: Float,
                             co2Ppm: Float,
                             isAnomaly: Boolean,
                             timestamp: Long,
                             ingestionTimestamp: Long,
                             deviceStatus: String
                           )

// ------------------------------
// 2. Import generated ScalaPB files (your structure)
// ------------------------------
import example.sensor_reading.SensorReading
import example.daily.daily_avg_pm.DailyAvgPM
import example.daily.daily_avg_co2.DailyAvgCO2
import example.daily.daily_anomaly_count.DailyAnomalyCount

// ------------------------------
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata

object PipelineD {

  def main(args: Array[String]): Unit = {

    println("🚀 Starting Pipeline D — Daily Dashboard Reports")

    val bucket = "smart-env-monitering-bucket"
    val prefix = "sensor-history/"
    val region = "us-east-1"

    val accessKey = "ACCESSKEY"
    val secretKey = "SECRETKEY"

    val jdbcUrl = "jdbc:mysql://DB_URL:3306/Smart_Environmental_MonitoringDB"
    val jdbcUser = "admin"
    val jdbcPass = "PASSWORD"

    val today = LocalDate.now().toString

    // Spark session
    val spark = SparkSession.builder()
      .appName("PipelineD-DailyReports")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.access.key", accessKey)
      .config("spark.hadoop.fs.s3a.secret.key", secretKey)
      .config("spark.hadoop.fs.s3a.endpoint", s"s3.$region.amazonaws.com")
      .getOrCreate()

    import spark.implicits._

    // AWS client
    val client = AmazonS3ClientBuilder.standard()
      .withRegion(region)
      .withCredentials(new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(accessKey, secretKey)
      ))
      .build()

    // ------------------------------------------
    // 3. Read protobuf files → decode → case class
    // ------------------------------------------
    val basePath = s"s3a://$bucket/$prefix"
    val fs = FileSystem.get(new URI(basePath), spark.sparkContext.hadoopConfiguration)

    val files = fs.listStatus(new Path(basePath)).map(_.getPath.toString)

    println(s"📦 Found ${files.size} protobuf files")

    val decoded = files.flatMap { file =>
      val stream = fs.open(new Path(file))
      val bytes = new Array[Byte](stream.available())
      stream.readFully(bytes)
      stream.close()

      Try(SensorReading.parseFrom(bytes)).toOption.map { pb =>
        SensorReadingRow(
          pb.readingId,
          pb.sensorId,
          pb.zoneId,
          pb.pm25,
          pb.pm10,
          pb.co2Ppm,
          pb.isAnomaly,
          pb.timestamp,
          pb.ingestionTimestamp,
          pb.deviceStatus
        )
      }
    }.toSeq.toDS()

    decoded.createOrReplaceTempView("readings")

    // ------------------------------------------
    // 4. Load MySQL Zones
    // ------------------------------------------
    val props = new java.util.Properties()
    props.put("user", jdbcUser)
    props.put("password", jdbcPass)
    props.put("driver", "com.mysql.cj.jdbc.Driver")

    val zonesDF = spark.read.jdbc(jdbcUrl, "zone", props)
    zonesDF.createOrReplaceTempView("zones")

    // ------------------------------------------
    // 5. DAILY REPORTS
    // ------------------------------------------
    val dailyPM = spark.sql(
        s"""
         SELECT r.zoneId, z.name AS zoneName, z.city,
                AVG(r.pm2_5) AS avg_pm25,
                AVG(r.pm10) AS avg_pm10,
                '$today' AS recordDate
         FROM readings r JOIN zones z ON r.zoneId = z.zone_id
         GROUP BY r.zoneId, z.name, z.city
       """)
      .as[(Int, String, String, Double, Double, String)]

    val dailyCO2 = spark.sql(
        s"""
         SELECT r.zoneId, z.name AS zoneName, z.city,
                AVG(r.co2Ppm) AS avg_co2,
                '$today' AS recordDate
         FROM readings r JOIN zones z ON r.zoneId = z.zone_id
         GROUP BY r.zoneId, z.name, z.city
       """)
      .as[(Int, String, String, Double, String)]

    val dailyAnom = spark.sql(
        s"""
         SELECT r.zoneId, z.name AS zoneName, z.city,
                SUM(CASE WHEN r.isAnomaly THEN 1 ELSE 0 END) AS anomalyCount,
                '$today' AS recordDate
         FROM readings r JOIN zones z ON r.zoneId = z.zone_id
         GROUP BY r.zoneId, z.name, z.city
       """)
      .as[(Int, String, String, Long, String)]

    // ------------------------------------------
    // 6. Upload helper
    // ------------------------------------------
    def upload(key: String, bytes: Array[Byte]): Unit = {
      val md = new ObjectMetadata()
      md.setContentLength(bytes.length)
      md.setContentType("application/octet-stream")

      val stream = new java.io.ByteArrayInputStream(bytes)
      client.putObject(bucket, key, stream, md)
      stream.close()
      println(s"✔ Uploaded: $key")
    }

    // ------------------------------------------
    // 7. Write OUTPUT PROTOBUF SUMMARIES
    // ------------------------------------------
    println("📝 Writing Daily PM Summary...")
    dailyPM.collect().foreach { case (zoneId, zoneName, city, pm25, pm10, date) =>
      val proto = DailyAvgPM(zoneId, zoneName, city, pm25.toFloat, pm10.toFloat, date)
      upload(s"dashboard/daily_avg_pm/$date/zone_$zoneId.pb", proto.toByteArray)
    }

    println("📝 Writing Daily CO2 Summary...")
    dailyCO2.collect().foreach { case (zoneId, zoneName, city, co2, date) =>
      val proto = DailyAvgCO2(zoneId, zoneName, city, co2.toFloat, date)
      upload(s"dashboard/daily_avg_co2/$date/zone_$zoneId.pb", proto.toByteArray)
    }

    println("📝 Writing Daily Anomaly Summary...")
    dailyAnom.collect().foreach { case (zoneId, zoneName, city, cnt, date) =>
      val proto = DailyAnomalyCount(zoneId, zoneName, city, cnt.toInt, date)
      upload(s"dashboard/daily_anomaly_counts/$date/zone_$zoneId.pb", proto.toByteArray)
    }

    println("✔ Pipeline D Completed Successfully")
    spark.stop()
  }
}
