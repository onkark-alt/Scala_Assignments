package Assignment2ß
import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object ProductDataPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Product Data Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (2M PRODUCTS)
    // ------------------------------------------------------
    val numProducts = 2000000
    val categories = Array("Electronics", "Clothes", "Books")

    val productRDD = spark.sparkContext
      .parallelize(1 to numProducts, 40)
      .map { id =>
        val cat = categories(Random.nextInt(categories.length))
        val price = Random.nextDouble() * 2000
        val desc = Random.alphanumeric.take(50).mkString
        (id.toLong, cat, price, desc)
      }

    val productDF = productRDD
      .toDF("productId", "category", "price", "description")

    // ------------------------------------------------------
    // 2. FILTER PRODUCTS WITH PRICE > 1000 (DataFrame)
    // ------------------------------------------------------
    val filteredDF = productDF.filter($"price" > 1000)

    println("Sample filtered rows:")
    filteredDF.show(5, truncate = false)

    // ------------------------------------------------------
    // 3. SORT BY PRICE (Broad shuffle)
    // ------------------------------------------------------
    val sortedDF = filteredDF.sort($"price".desc)

    println("Sorted sample:")
    sortedDF.show(5, truncate = false)

    // ------------------------------------------------------
    // 4. WRITE SORTED DATA TO CSV
    // ------------------------------------------------------
    sortedDF.write
      .mode(SaveMode.Overwrite)
      .csv("output/products_sorted_csv")

    // ------------------------------------------------------
    // 5. WRITE SORTED DATA TO PARQUET
    // ------------------------------------------------------
    sortedDF.write
      .mode(SaveMode.Overwrite)
      .parquet("output/products_sorted_parquet")
    Thread.sleep(30000)

    spark.stop()
  }
}
