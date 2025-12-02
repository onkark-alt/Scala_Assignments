package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object TransactionAnalyticsPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Transaction Analytics Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (3 MILLION TRANSACTIONS)
    // ------------------------------------------------------
    val numTxns = 3000000

    val txnRDD = spark.sparkContext
      .parallelize(1 to numTxns, 40)
      .map { id =>
        val acc = "ACC_" + Random.nextInt(100000)
        val amt = Random.nextDouble() * 10000
        (acc, amt)
      }

    val txnDF = txnRDD.toDF("accountId", "amount")

    // ------------------------------------------------------
    // 2. RDD APPROACH: reduceByKey → sortBy
    // ------------------------------------------------------
    val rddTotals = txnRDD
      .reduceByKey(_ + _)         // BROAD (shuffle)
      .sortBy(_._2, ascending = false) // BROAD (shuffle)
      .take(10)

    println("RDD Top 10 Accounts:")
    rddTotals.foreach(println)

    // ------------------------------------------------------
    // 3. DATAFRAME APPROACH: groupBy → sum → orderBy
    // ------------------------------------------------------
    val dfTotals = txnDF
      .groupBy("accountId")            // BROAD (shuffle)
      .sum("amount")
      .withColumnRenamed("sum(amount)", "totalSpent")
      .orderBy($"totalSpent".desc)     // BROAD (shuffle)
      .limit(10)

    println("DataFrame Top 10 Accounts:")
    dfTotals.show(false)
    Thread.sleep(300000)
    spark.stop()
  }
}

