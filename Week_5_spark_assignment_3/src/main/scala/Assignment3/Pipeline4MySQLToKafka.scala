package Assignment3

import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.avro.to_avro

object Pipeline4MySQLToKafka extends App {

  // -------------------------------------------
  // 1. Spark Session
  // -------------------------------------------
  val spark = SparkSession.builder()
    .appName("MySQL-to-Kafka-Avro-Streaming")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // -------------------------------------------
  // 2. MySQL JDBC Config
  // -------------------------------------------
  val jdbcUrl = "jdbc:mysql://database:3306/dbname"
  val jdbcUser = "admin"
  val jdbcPass = "password"

  // Track last processed ID
  var lastMaxOrderId = 0

  // Dummy stream used only as a timer (5 sec trigger)
  val heartbeatStream = spark.readStream
    .format("rate")
    .option("rowsPerSecond", 1)
    .load()

  // -------------------------------------------
  // 3. Main Streaming Logic using foreachBatch
  // -------------------------------------------
  val query = heartbeatStream.writeStream
    .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>

      println("\n⏳ Checking MySQL for new rows...")

      // Load entire table (small table assumed)
      val mysqlDF = spark.read
        .format("jdbc")
        .option("url", jdbcUrl)
        .option("dbtable", "new_orders")
        .option("user", jdbcUser)
        .option("password", jdbcPass)
        .load()

      // New rows only
      val newRows = mysqlDF.filter(col("order_id") > lastMaxOrderId)

      if (newRows.count() > 0) {

        println(s"🚀 Found ${newRows.count()} new rows:")
        newRows.show(false)

        // Update max order_id
        lastMaxOrderId = newRows.agg(max("order_id")).first().getInt(0)

        // Encode using Avro schema
        val avroDF = newRows.select(
          to_avro(struct($"order_id", $"customer_id", $"amount", $"created_at"))
            .as("value")
        )

        // -------------------------------------------
        // 4. Write to Kafka as Avro messages
        // -------------------------------------------
        avroDF.write
          .format("kafka")
          .option("kafka.bootstrap.servers", "localhost:9092")
          .option("topic", "orders_avro_topic")
          .save()

        println(s"📨 Successfully sent ${newRows.count()} Avro messages to Kafka!")
      }
      else {
        println("✔ No new rows found.")
      }

    }
    .trigger(org.apache.spark.sql.streaming.Trigger.ProcessingTime("5 seconds"))
    .start()

  query.awaitTermination()
}