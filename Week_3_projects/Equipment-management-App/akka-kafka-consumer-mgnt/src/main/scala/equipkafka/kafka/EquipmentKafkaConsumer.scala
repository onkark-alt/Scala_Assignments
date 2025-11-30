package equipkafka.kafka

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffsetBatch}
import equipkafka.actors.NotificationProtocol.Handle
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig

object EquipmentKafkaConsumer {

  def start(
             system: ActorSystem,
             allocatedActor: ActorRef,
             returnedActor: ActorRef,
             damagedActor: ActorRef
           ): Unit = {

    implicit val mat: Materializer = Materializer(system)
    implicit val ec = system.dispatcher

    val config = system.settings.config
    val bootstrap = config.getString("kafka.bootstrap-servers")
    val groupId = config.getString("kafka.group-id")

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrap)
        .withGroupId(groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    val topicConfig = config.getConfig("topics")
    val tAllocated = topicConfig.getString("allocated")
    val tReturned  = topicConfig.getString("returned")
    val tDamaged   = topicConfig.getString("damaged")

    val subscription = Subscriptions.topics(tAllocated, tReturned, tDamaged)

    Consumer
      .committableSource(consumerSettings, subscription)
      .mapAsync(1) { msg: CommittableMessage[String, String] =>

        val topic = msg.record.topic()
        val value = msg.record.value()

        system.log.info(s"[Kafka] Received message: topic=$topic value=$value")

        topic match {
          case t if t == tAllocated => allocatedActor ! Handle(value, topic)
          case t if t == tReturned  => returnedActor  ! Handle(value, topic)
          case t if t == tDamaged   => damagedActor   ! Handle(value, topic)
          case other =>
            system.log.warning(s"Unexpected topic: $other")
        }

        scala.concurrent.Future.successful(msg.committableOffset)
      }
      .batch(
        max = 20,
        seed = first => CommittableOffsetBatch.empty.updated(first)
      ) { (batch, elem) =>
        batch.updated(elem)
      }
      .mapAsync(1)(_.commitScaladsl())
      .runWith(Sink.ignore)

    system.log.info(s"Kafka consumer started for: $tAllocated, $tReturned, $tDamaged")
  }
}
