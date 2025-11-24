package equipkafka.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import play.api.libs.json._
import equipkafka.utils.NotificationHelper
import equipkafka.services.EmailService

object EquipmentReturnedActor {
  import NotificationProtocol._

  def apply(): Behavior[Handle] = Behaviors.setup { ctx =>
    val email = new EmailService()

    Behaviors.receiveMessage { msg =>
      ctx.log.info(s"[Returned] ${msg.json}")

      val js = Json.parse(msg.json)
      val allocationId = (js \ "allocationId").asOpt[Long]
      val equipmentId  = (js \ "equipmentId").asOpt[Long]
      val condition    = (js \ "returnedCondition").asOpt[String]

      NotificationHelper.handleReturn(allocationId, equipmentId, condition)

      email.sendEmail(
        "onkar.vallal.9@gmail.com",
        "Equipment Returned",
        s"Equipment $equipmentId returned.\nCondition: $condition\n\n${msg.json}"
      )

      Behaviors.same
    }
  }
}
