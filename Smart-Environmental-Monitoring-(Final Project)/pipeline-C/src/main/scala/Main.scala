import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.functions._
import example.sensor_reading.SensorReading
import java.time.LocalDate

object Main {

  def main(args: Array[String]): Unit = {

    println("======== PIPELINE C STARTED ========")

    // ------------------------------------------------------------------
    // 0) SPARK SESSION + S3 CONFIG
    // ------------------------------------------------------------------
    println("[INFO] Initializing Spark session...")

    val spark = SparkSession.builder()
      .appName("PipelineC-DailyPollutionSummary")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.access.key", "ACCESSKEY")
      .config("spark.hadoop.fs.s3a.secret.key", "SECRETKEY")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.region", "us-east-1")
      .getOrCreate()

    println("[INFO] Spark session created successfully.")
    import spark.implicits._

    val recordDate = LocalDate.now().toString
    val s3Path = "s3a://smart-env-monitering-bucket/sensor-history/*.pb"

    println(s"[INFO] Record Date: $recordDate")
    println(s"[INFO] S3 Input Path: $s3Path")

    // ------------------------------------------------------------------
    // 1) LOAD PROTOBUF FILES AS BINARY
    // ------------------------------------------------------------------
    println("[INFO] Reading protobuf files from S3...")

    val binaryDF = spark.read.format("binaryFile")
      .option("recursiveFileLookup", "true")
      .load(s3Path)

    println("[INFO] Raw binary files loaded.")
    println(s"[DEBUG] Number of protobuf files found: ${binaryDF.count()}")

    binaryDF.printSchema()

    // ------------------------------------------------------------------
    // 2) Decode Protobuf into a readable DF
    // ------------------------------------------------------------------
    println("[INFO] Decoding protobuf messages...")

    val decoded = binaryDF.map { row =>
      val bytes = row.getAs[Array[Byte]]("content")
      val sr = SensorReading.parseFrom(bytes)

      (
        sr.zoneId,
        sr.pm25,
        sr.pm10,
        sr.co2Ppm,
        sr.isAnomaly
      )
    }.toDF("zone_id", "pm2_5", "pm10", "co2", "is_anomaly")

    println(s"[INFO] Decoded ${decoded.count()} protobuf records.")
    decoded.printSchema()
    decoded.show(10, false)

    decoded.createOrReplaceTempView("readings")

    // ------------------------------------------------------------------
    // 3) LOAD ZONES + THRESHOLDS FROM MYSQL
    // ------------------------------------------------------------------
    println("[INFO] Loading MySQL dimension tables...")

    val jdbcUrl = "jdbc:mysql://DB_URL:3306/Smart_Environmental_MonitoringDB"
    val props = new java.util.Properties()
    props.put("user", "admin")
    props.put("password", "PASSWORD")
    props.put("driver", "com.mysql.cj.jdbc.Driver")

    val zonesDF = spark.read.jdbc(jdbcUrl, "zone", props)
    val thresholdDF = spark.read.jdbc(jdbcUrl, "pollution_threshold", props)

    println("[INFO] Loaded MySQL tables successfully.")
    println(s"[DEBUG] Zones table count: ${zonesDF.count()}")
    println(s"[DEBUG] Thresholds table count: ${thresholdDF.count()}")

    zonesDF.printSchema()
    thresholdDF.printSchema()

    zonesDF.createOrReplaceTempView("zones")
    thresholdDF.createOrReplaceTempView("thresholds")

    // ------------------------------------------------------------------
    // 4) JOIN + AGGREGATION
    // ------------------------------------------------------------------
    println("[INFO] Running aggregation SQL...")

    val summaryDF = spark.sql(
      s"""
        SELECT
            r.zone_id,
            AVG(r.pm2_5) AS avg_pm2_5,
            AVG(r.pm10) AS avg_pm10,
            AVG(r.co2) AS avg_co2,
            SUM(CASE WHEN r.is_anomaly THEN 1 ELSE 0 END) AS anomaly_count,
            '$recordDate' AS record_date
        FROM readings r
        JOIN zones z ON r.zone_id = z.zone_id
        JOIN thresholds t ON r.zone_id = t.zone_id
        GROUP BY r.zone_id
      """
    )

    println("[INFO] Aggregation complete.")
    println(s"[DEBUG] Summary rows produced: ${summaryDF.count()}")

    summaryDF.printSchema()
    summaryDF.show(false)

    // ------------------------------------------------------------------
    // 5) WRITE SUMMARY TO MYSQL
    // ------------------------------------------------------------------
    println("[INFO] Writing to MySQL table: daily_pollution_summary")

    summaryDF.write
      .mode("append")
      .jdbc(jdbcUrl, "daily_pollution_summary", props)

    println("[SUCCESS] Data successfully inserted into daily_pollution_summary.")
    println("======== PIPELINE C COMPLETED ========")

    spark.stop()
  }
}

