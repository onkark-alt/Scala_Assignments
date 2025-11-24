package equipkafka.bootstrap

import akka.actor.typed.{Behavior, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import equipkafka.kafka.EquipmentKafkaConsumer
import equipkafka.actors._
import scala.concurrent.ExecutionContext

sealed trait RootCommand

object RootBehavior {

  def apply(): Behavior[RootCommand] =
    Behaviors.setup[RootCommand] { ctx =>

      implicit val system: ActorSystem[Nothing] = ctx.system
      implicit val ec: ExecutionContext = ctx.executionContext

      ctx.log.info("RootBehavior initialized. Starting Kafka consumer…")

      val allocatedActor = ctx.spawn(EquipmentAllocatedActor(), "equipmentAllocatedActor")
      val returnedActor  = ctx.spawn(EquipmentReturnedActor(),  "equipmentReturnedActor")
      val damagedActor   = ctx.spawn(EquipmentDamagedActor(),   "equipmentDamagedActor")

      // Start Kafka consumer stream
      EquipmentKafkaConsumer.start(
        system,
        allocatedActor,
        returnedActor,
        damagedActor
      )

      // ❗ KEEP SYSTEM ALIVE — never stop the behavior
      Behaviors.receiveMessage { _ =>
        Behaviors.same
      }
    }
}
