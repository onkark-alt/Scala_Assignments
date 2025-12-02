package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object LogDataPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Log Data Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (5M LOGS)
    // ------------------------------------------------------
    val levels = Array("INFO", "WARN", "ERROR")
    val numLogs = 5000000

    val logsRDD = spark.sparkContext
      .parallelize(1 to numLogs, 40)
      .map { _ =>
        val ts = System.currentTimeMillis() - Random.nextInt(10000000)
        val level = levels(Random.nextInt(levels.length))
        val msg = Random.alphanumeric.take(15).mkString
        val user = Random.nextInt(10000)
        s"$ts|$level|$msg|$user"
      }

    val logsDF = logsRDD
      .map(_.split("\\|"))
      .map(a => (a(0), a(1), a(2), a(3)))
      .toDF("timestamp", "level", "message", "userId")

    // ------------------------------------------------------
    // 2. COUNT ERROR LOGS USING RDD
    // ------------------------------------------------------
    val errorCountRDD = logsRDD
      .filter(line => line.contains("|ERROR|"))
      .count()

    println(s"RDD ERROR Count = $errorCountRDD")

    // ------------------------------------------------------
    // 3. COUNT ERROR LOGS USING DATAFRAME
    // ------------------------------------------------------
    val errorCountDF = logsDF
      .filter($"level" === "ERROR")
      .count()

    println(s"DataFrame ERROR Count = $errorCountDF")

    // ------------------------------------------------------
    // 4. WRITE ERROR LOGS TO PLAIN TEXT
    // ------------------------------------------------------
    logsRDD
      .filter(_.contains("|ERROR|"))
      .saveAsTextFile("output/error_logs_text")

    // ------------------------------------------------------
    // 5. WRITE FULL LOG DATA TO JSON
    // ------------------------------------------------------
    logsDF.write
      .mode(SaveMode.Overwrite)
      .json("output/full_logs_json")

    Thread.sleep(300000)

    spark.stop()
  }
}