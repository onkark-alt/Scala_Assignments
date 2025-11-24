package equipkafka.kafka

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffsetBatch}
import equipkafka.actors.NotificationProtocol.Handle
import org.apache.kafka.clients.consumer.ConsumerConfig

object EquipmentKafkaConsumer {

  def start(
             system: ActorSystem[_],
             allocatedActor: ActorRef[Handle],
             returnedActor: ActorRef[Handle],
             damagedActor: ActorRef[Handle]
           ): Unit = {

    implicit val mat: Materializer = Materializer(system)
    implicit val ec = system.executionContext

    val config = system.settings.config
    val bootstrap = config.getString("kafka.bootstrap-servers")
    val groupId = config.getString("kafka.group-id")

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrap)
        .withGroupId(groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    val topicsCfg = config.getConfig("topics")
    val tAllocated = topicsCfg.getString("allocated")
    val tReturned  = topicsCfg.getString("returned")
    val tDamaged   = topicsCfg.getString("damaged")

    val subscription = Subscriptions.topics(tAllocated, tReturned, tDamaged)

    Consumer
      .committableSource(consumerSettings, subscription)
      .mapAsync(1) { msg: CommittableMessage[String, String] =>  // fixed: no config.read
        val topic = msg.record.topic()
        val value = msg.record.value()
        system.log.info(s"[Kafka] Received message on topic=$topic: $value")
        topic match {
          case t if t == tAllocated => allocatedActor ! Handle(value, topic)
          case t if t == tReturned  => returnedActor  ! Handle(value, topic)
          case t if t == tDamaged   => damagedActor   ! Handle(value, topic)
          case other =>
            system.log.warn(s"[WARN] Unexpected topic: $other")
        }

        scala.concurrent.Future.successful(msg.committableOffset)
      }
      .batch(
        max = 20,        // default batch size
        seed = first => CommittableOffsetBatch.empty.updated(first)
      ) { (batch, elem) =>
        batch.updated(elem)
      }
      .mapAsync(1)(_.commitScaladsl()) // fixed: no config.read
      .runWith(Sink.ignore)

    system.log.info(s"Kafka consumer started for topics: $tAllocated, $tReturned, $tDamaged")
  }
}
