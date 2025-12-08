package Assignment

import events.user_event.user_event.UserEvent

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaUserEventProducer {
  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val producer = new KafkaProducer[String, Array[Byte]](props)

    val events = List(
      UserEvent("U1", "login", 30.5),
      UserEvent("U2", "purchase", 99.9),
      UserEvent("U3", "logout", 0.0),
      UserEvent("U1", "purchase", 150.4),
      UserEvent("U2", "login", 20.0)
    )

    events.foreach { e =>
      val record = new ProducerRecord[String, Array[Byte]](
        "user-events",
        e.userId,
        e.toByteArray
      )
      producer.send(record)
    }
    producer.close()
  }
}
