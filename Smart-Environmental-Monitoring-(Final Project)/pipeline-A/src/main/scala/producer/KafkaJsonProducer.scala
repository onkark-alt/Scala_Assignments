package producer

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import io.circe.syntax._
import io.circe.generic.auto._
import java.util.Properties

object KafkaJsonProducer {
  private val props = new Properties()
  props.put("bootstrap.servers", config.AppConfig.kafkaBootstrap)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  def send[T: io.circe.Encoder](topic: String, key: String, value: T): Unit = {
    val json = value.asJson.noSpaces
    producer.send(new ProducerRecord(topic, key, json))
  }
}
