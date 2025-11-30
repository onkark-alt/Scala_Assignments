package equipkafka.actors

import akka.actor.{Actor, ActorLogging}
import play.api.libs.json._
import equipkafka.services.EmailService
import equipkafka.utils.NotificationHelper

class EquipmentReturnedActor extends Actor with ActorLogging {
  import NotificationProtocol._

  val email = new EmailService()

  override def receive: Receive = {
    case Handle(json, topic) =>
      log.info(s"[Returned] $json")

      val js = Json.parse(json)
      val allocationId = (js \ "allocationId").asOpt[Long]
      val equipmentId  = (js \ "equipmentId").asOpt[Long]
      val condition    = (js \ "returnedCondition").asOpt[String]

      NotificationHelper.handleReturn(allocationId, equipmentId, condition)

      email.sendEmail(
        "onkar.vallal.9@gmail.com",
        "Equipment Returned",
        s"Equipment $equipmentId returned.\nCondition: $condition\n\n$json"
      )
  }
}
