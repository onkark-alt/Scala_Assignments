package equipkafka.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ActorSystem}
import equipkafka.kafka.EquipmentKafkaConsumer
import scala.concurrent.ExecutionContext

class RootSupervisorActor extends Actor with ActorLogging {

  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContext = context.dispatcher

  override def preStart(): Unit = {
    log.info("RootSupervisorActor initialized. Starting Kafka consumer…")

    val allocatedActor = context.actorOf(Props[EquipmentAllocatedActor], "equipmentAllocatedActor")
    val returnedActor  = context.actorOf(Props[EquipmentReturnedActor],  "equipmentReturnedActor")
    val damagedActor   = context.actorOf(Props[EquipmentDamagedActor],   "equipmentDamagedActor")

    // Start Kafka Consumer
    EquipmentKafkaConsumer.start(
      system,
      allocatedActor,
      returnedActor,
      damagedActor
    )
  }

  override def receive: Receive = {
    case msg => log.info(s"Root received: $msg")
  }
}

object RootSupervisorActor {
  def props(): Props = Props(new RootSupervisorActor)
}
