package equipkafka.actors

import akka.actor.{Actor, ActorLogging}
import play.api.libs.json._
import equipkafka.utils.NotificationHelper
import equipkafka.services.EmailService

class EquipmentAllocatedActor extends Actor with ActorLogging {
  import NotificationProtocol._

  val email = new EmailService()

  override def receive: Receive = {
    case Handle(json, topic) =>
      log.info(s"[Allocated] $json")

      val js = Json.parse(json)
      val equipmentId = (js \ "equipmentId").asOpt[Long]
      val employeeId  = (js \ "employeeId").asOpt[Long]

      NotificationHelper.notifyInventory(equipmentId, employeeId)

      email.sendEmail(
        "onkar.k@payoda.com",
        "Equipment Allocated",
        s"Equipment $equipmentId allocated to $employeeId\nPayload:\n$json"
      )
  }
}
