package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object SensorDataPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Sensor Data Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (3 MILLION SENSOR READINGS)
    // ------------------------------------------------------
    val numSensors = 3000000

    val sensorRDD = spark.sparkContext
      .parallelize(1 to numSensors, 40)
      .map { _ =>
        val dev = "DEV_" + Random.nextInt(5000)
        val temp = 20 + Random.nextDouble() * 15
        val hum = 40 + Random.nextDouble() * 20
        val hour = Random.nextInt(24)
        (dev, temp, hum, hour)
      }

    val sensorDF = sensorRDD
      .toDF("deviceId", "temperature", "humidity", "hour")

    // ------------------------------------------------------
    // 2. AVG TEMPERATURE PER HOUR USING DATAFRAME
    // ------------------------------------------------------
    val avgTempDF = sensorDF
      .groupBy("hour")
      .avg("temperature")
      .withColumnRenamed("avg(temperature)", "avgTemperature")

    println("Sample output:")
    avgTempDF.show(24, truncate = false)

    // ------------------------------------------------------
    // 3. WRITE TO PARQUET PARTITIONED BY HOUR
    // ------------------------------------------------------
    avgTempDF.write
      .mode(SaveMode.Overwrite)
      .partitionBy("hour")
      .parquet("output/sensor_avg_temp_parquet")
      Thread.sleep(30000)
    spark.stop()
  }
}

