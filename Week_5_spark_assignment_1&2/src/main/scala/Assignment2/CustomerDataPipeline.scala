package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object CustomerDataPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Customer Data Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -----------------------------
    // 1. DATA GENERATION (5M RECORDS)

    val numRecords = 5000000
    val cities = (1 to 50).map(i => s"City_$i").toArray

    val customersRDD = spark.sparkContext
      .parallelize(1 to numRecords, 50)
      .map { id =>
        val name = Random.alphanumeric.take(10).mkString
        val age = 18 + Random.nextInt(53)
        val city = cities(Random.nextInt(cities.length))
        (id.toLong, name, age, city)
      }

    val customersDF = customersRDD.toDF("customerId", "name", "age", "city")

    // -----------------------------
    // 2. RDD OPERATION – COUNT PER CITY
    // -----------------------------
    val cityCountRDD = customersRDD
      .map { case (_, _, _, city) => (city, 1) }
      .reduceByKey(_ + _)

    println("RDD Output Sample:")
    cityCountRDD.take(10).foreach(println)

    // -----------------------------
    // 3. DATAFRAME OPERATION – COUNT PER CITY
    // -----------------------------
    val cityCountDF = customersDF
      .groupBy("city")
      .count()

    cityCountDF.show(10, truncate = false)

    // -----------------------------
    // 4. WRITE OUTPUTS
    // -----------------------------
    cityCountDF.write.mode(SaveMode.Overwrite).csv("output/city_count_csv")
    cityCountDF.write.mode(SaveMode.Overwrite).json("output/city_count_json")
    cityCountDF.write.mode(SaveMode.Overwrite).parquet("output/city_count_parquet")
    Thread.sleep(300000)
    spark.stop()
  }
}
