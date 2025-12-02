package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object SalesDataPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Sales Data Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (10 MILLION SALES RECORDS)
    // ------------------------------------------------------
    val numSales = 10000000
    val stores = (1 to 100).map(i => s"Store_$i").toArray

    val salesRDD = spark.sparkContext
      .parallelize(1 to numSales, 50)
      .map { id =>
        val store = stores(Random.nextInt(stores.length))
        val amt = Random.nextDouble() * 500
        (store, amt)
      }

    val salesDF = salesRDD.toDF("storeId", "amount")

    // ------------------------------------------------------
    // 2. RDD COMPARISON: groupByKey vs reduceByKey
    // ------------------------------------------------------

    // ❗ groupByKey (very slow + heavy shuffle)
    val grouped = salesRDD.groupByKey()

    // ❗ reduceByKey (much faster)
    val reduced = salesRDD.reduceByKey(_ + _)

    println("Sample from groupByKey:")
    grouped.take(5).foreach(println)

    println("Sample from reduceByKey:")
    reduced.take(5).foreach(println)

    // ------------------------------------------------------
    // 3. DATAFRAME: Compute store-wise total sales
    // ------------------------------------------------------
    val storeTotalsDF = salesDF
      .groupBy("storeId")
      .sum("amount")
      .withColumnRenamed("sum(amount)", "totalSales")

    storeTotalsDF.show(10, truncate = false)

    // ------------------------------------------------------
    // 4. WRITE OUTPUT (PARQUET)
    // ------------------------------------------------------
    storeTotalsDF.write
      .mode(SaveMode.Overwrite)
      .parquet("output/store_sales_parquet")
      Thread.sleep(300000)
    spark.stop()
  }
}
