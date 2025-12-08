package Assignment

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf

object KafkaUserEventReader {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Kafka User Event Reader")
      .master("local[*]")
      .getOrCreate()

    val descPath = "src/main/resources/desc/UserEvent.desc"

    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "user-events")
      .option("startingOffsets", "earliest")
      .load()

    val decoded = kafkaDF
      .select(from_protobuf(col("value"), "events.user_event.UserEvent", descPath).as("event"))
      .filter(col("event").isNotNull)

    val flattened = decoded.select(
      col("event.userId"),
      col("event.action"),
      col("event.value")
    )

    val eventsPerAction = flattened.groupBy("action").count()

    val topUsers = flattened.groupBy("userId")
      .agg(sum("value").as("totalValue"))
      .orderBy(col("totalValue").desc)
      .limit(5)

    eventsPerAction.writeStream.outputMode("complete").format("console").start()
    topUsers.writeStream.outputMode("complete").format("console").start()

    spark.streams.awaitAnyTermination()
  }
}
