package Assignment3



import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Pipeline1_RDS_To_Keyspaces extends App {

  val spark = SparkSession.builder
    .appName("RDS to Keyspaces Pipeline")
    .master("local[*]")
    // Cassandra connection
    .config("spark.cassandra.connection.host", "cassandra.us-east-1.amazonaws.com")
    .config("spark.cassandra.connection.port", "9142")
    .config("spark.cassandra.connection.ssl.enabled", "true")

    // Username / password generated in AWS
    .config("spark.cassandra.auth.username", "user")
    .config("spark.cassandra.auth.password", "password")

    // Consistency recommended by AWS
    .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")

    // Truststore generated on your Mac
    .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/abc/cassandra_truststore.jks")
    .config("spark.cassandra.connection.ssl.trustStore.password", "changeit")


    .getOrCreate()

  // --------------------- MySQL JDBC -------------------------
  val jdbcUrl = "jdbc:mysql://database:3306/dbname"

  val jdbcProps = new java.util.Properties()
  jdbcProps.setProperty("user", "admin")
  jdbcProps.setProperty("password", "password")
  jdbcProps.setProperty("driver", "com.mysql.cj.jdbc.Driver")

  val customersDF = spark.read.jdbc(jdbcUrl, "customers", jdbcProps)
  val ordersDF    = spark.read.jdbc(jdbcUrl, "orders", jdbcProps)
  val itemsDF     = spark.read.jdbc(jdbcUrl, "order_items", jdbcProps)

  // --------------------- Join Tables -------------------------
  val finalDF =
    customersDF
      .join(ordersDF, "customer_id")
      .join(itemsDF, "order_id")
      .select(
        col("customer_id"),
        col("name"),
        col("email"),
        col("city"),
        col("order_id"),
        col("order_date").cast("timestamp"),
        col("amount"),
        col("item_id"),
        col("product_name"),
        col("quantity")
      )

  // --------------------- Write to Keyspaces -------------------------
  finalDF.write
    .format("org.apache.spark.sql.cassandra")
    .option("keyspace", "retail")
    .option("table", "customers")
    .mode("append")
    .save()

  spark.stop()
}