import org.apache.spark.sql.SparkSession

object WriteToMySQLPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Write To MySQL")
      .master("local[*]")
      .getOrCreate()

    // Load df2 from previous pipeline
    val df2 = spark.read
      .parquet("/Users/racit/Documents/SPARK/spark-examples-main/trips_with_duration_parquet")

    // Write to MySQL
    df2.limit(10)
      .write
      .format("jdbc")
      .option("url", "jdbc:mysql://mysql8823.mysql.database.azure.com/db_name")
      .option("dbtable", "trip_summary")
      .option("user", "admin")
      .option("password", "Password")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .mode("append")
      .save()


    spark.stop()
  }
}