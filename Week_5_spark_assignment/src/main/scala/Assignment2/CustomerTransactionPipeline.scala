package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object CustomerTransactionPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Customer Transaction Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -------------------------------------
    // ✅ Data Generation: Customers (2M)
    // -------------------------------------
    val custCount = 2000000

    val custRDD = spark.sparkContext.parallelize(1 to custCount, 50)
      .map { id =>
        val name = Random.alphanumeric.take(8).mkString
        (id, name)
      }

    val custDF = custRDD.toDF("customerId", "name")


    // -------------------------------------
    // ✅ Data Generation: Transactions (5M)
    // -------------------------------------
    val txnCount = 5000000

    val txnRDD = spark.sparkContext.parallelize(1 to txnCount, 80)
      .map { tid =>
        val cust = Random.nextInt(custCount) + 1
        val amt = Random.nextDouble() * 1000
        (tid, cust, amt)
      }

    val txnDF = txnRDD.toDF("txnId", "customerId", "amount")


    // -------------------------------------
    // 📘 Exercise: Join customers & txns
    // -------------------------------------
    val joinedDF = custDF
      .join(txnDF, "customerId")   // Natural join on customerId


    // -------------------------------------
    // 📘 Calculate total spend per customer
    // -------------------------------------
    val totalSpendDF = joinedDF
      .groupBy("customerId")
      .sum("amount")
      .withColumnRenamed("sum(amount)", "totalSpent")


    // -------------------------------------
    // 📘 Save to Parquet (best for analytics)
    // -------------------------------------
    totalSpendDF.write
      .mode(SaveMode.Overwrite)
      .parquet("output/customer_total_spend_parquet")

    println("🔄 Join + GroupBy completed (large shuffle).")
    println("📁 Output written to Parquet at: output/customer_total_spend_parquet")
    Thread.sleep(300000)
    spark.stop()
  }
}

