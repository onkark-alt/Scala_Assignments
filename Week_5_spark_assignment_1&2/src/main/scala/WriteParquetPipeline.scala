import org.apache.spark.sql.{SparkSession, functions => F}

object WriteParquetPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Write Parquet Pipeline")
      .master("local[*]")
      .getOrCreate()

    // Load previous pipeline output
    val df2 = spark.read.parquet(
      "/Users/racit/Documents/SPARK/spark-examples-main/trips_with_duration_parquet"
    )

    // Save DF to Parquet
    df2.write
      .mode("overwrite")
      .parquet("/Users/racit/Documents/SPARK/spark-examples-main/parquet/trips_clean.parquet")

    println("Parquet write completed!")
    spark.stop()
  }
}