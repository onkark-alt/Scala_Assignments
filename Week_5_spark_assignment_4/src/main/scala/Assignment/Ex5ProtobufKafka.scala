//package Assignment
//
//import org.apache.spark.sql.{Row, SparkSession}
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types._
//
//import events.user_event.UserEvent
//   // <-- this is the correct package
// // <-- this is the correct package
//
//
//object Ex5ProtobufKafka {
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder()
//      .appName("Exercise5ProtobufWithKafka")
//      .master("local[*]")
//      .getOrCreate()
//
//    import spark.implicits._
//
//    // 1) Read messages from Kafka (value is binary bytes)
//    val kafkaDF = spark.read
//      .format("kafka")
//      .option("kafka.bootstrap.servers", "localhost:9092")
//      .option("subscribe", "user-events")
//      .option("startingOffsets", "earliest")
//      .load()
//
//    // Value is binary input to protobuf parsing
//    val rawEvents = kafkaDF.select($"value".as("bytes"))
//
//    // 2) UDF to deserialize protobuf
//    val parseUserEvent = udf((bytes: Array[Byte]) => {
//      try {
//        val event = UserEvent.parseFrom(bytes)
//        Row(event.userId, event.action, event.value)
//      } catch {
//        case _: Throwable => null // malformed messages
//      }
//    })
//
//    // Schema matching the Row returned by UDF
//    val eventSchema = StructType(Seq(
//      StructField("userId", StringType),
//      StructField("action", StringType),
//      StructField("value", DoubleType)
//    ))
//
//    // Apply UDF → convert to DataFrame
//    val eventsDF = rawEvents
//      .withColumn("event", parseUserEvent(col("bytes")))
//      .select("event.*")
//      .na.drop() // Drop malformed protobuf rows
//
//    // 3) Count number of events per action
//    val eventsPerAction = eventsDF
//      .groupBy("action")
//      .count()
//      .orderBy(desc("count"))
//
//    println("=== Events per action ===")
//    eventsPerAction.show(false)
//
//    // 4) Top 5 users with highest value
//    val topUsers = eventsDF
//      .groupBy("userId")
//      .agg(sum("value").as("totalValue"))
//      .orderBy(desc("totalValue"))
//      .limit(5)
//
//    println("=== Top 5 users by total value ===")
//    topUsers.show(false)
//
//    spark.stop()
//  }
//}
