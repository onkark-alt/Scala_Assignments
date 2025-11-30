package equipkafka.actors

import akka.actor.{Actor, ActorLogging}
import play.api.libs.json._
import equipkafka.utils.NotificationHelper
import equipkafka.services.EmailService

class EquipmentDamagedActor extends Actor with ActorLogging {
  import NotificationProtocol._

  val email = new EmailService()

  override def receive: Receive = {
    case Handle(json, topic) =>
      log.info(s"[Damaged] $json")

      val js = Json.parse(json)
      val equipmentId = (js \ "equipmentId").asOpt[Long]
      val notes       = (js \ "notes").asOpt[String]

      NotificationHelper.alertMaintenance(equipmentId, notes)

      email.sendEmail(
        "maintenance@gmail.com",
        "⚠ Equipment Damaged",
        s"Equipment $equipmentId damaged.\nNotes: $notes\n\n$json"
      )
  }
}
