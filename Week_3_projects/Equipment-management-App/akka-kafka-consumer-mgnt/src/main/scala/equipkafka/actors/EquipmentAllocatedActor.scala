package equipkafka.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import play.api.libs.json._
import equipkafka.utils.NotificationHelper
import equipkafka.services.EmailService

object EquipmentAllocatedActor {
  import NotificationProtocol._

  def apply(): Behavior[Handle] = Behaviors.setup { ctx =>
    val email = new EmailService()

    Behaviors.receiveMessage { msg =>
      ctx.log.info(s"[Allocated] ${msg.json}")

      val js = Json.parse(msg.json)
      val equipmentId = (js \ "equipmentId").asOpt[Long]
      val employeeId  = (js \ "employeeId").asOpt[Long]

      NotificationHelper.notifyInventory(equipmentId, employeeId)

      email.sendEmail(
        "onkar.k@payoda.com",
        "Equipment Allocated",
        s"Equipment $equipmentId allocated to $employeeId\nPayload:\n${msg.json}"
      )

      Behaviors.same
    }
  }
}
