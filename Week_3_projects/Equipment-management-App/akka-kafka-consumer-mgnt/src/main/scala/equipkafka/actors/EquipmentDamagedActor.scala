package equipkafka.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import play.api.libs.json._
import equipkafka.utils.NotificationHelper
import equipkafka.services.EmailService

object EquipmentDamagedActor {
  import NotificationProtocol._

  def apply(): Behavior[Handle] = Behaviors.setup { ctx =>
    val email = new EmailService()

    Behaviors.receiveMessage { msg =>
      ctx.log.info(s"[Damaged] ${msg.json}")

      val js = Json.parse(msg.json)
      val equipmentId = (js \ "equipmentId").asOpt[Long]
      val notes       = (js \ "notes").asOpt[String]

      NotificationHelper.alertMaintenance(equipmentId, notes)

      email.sendEmail(
        "maintenance@gmail.com",
        "⚠ Equipment Damaged",
        s"Equipment $equipmentId damaged.\nNotes: $notes\n\n${msg.json}"
      )

      Behaviors.same
    }
  }
}
