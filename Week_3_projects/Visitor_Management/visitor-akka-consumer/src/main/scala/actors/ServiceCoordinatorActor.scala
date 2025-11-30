package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import spray.json._
import spray.json.DefaultJsonProtocol._

class ServiceCoordinatorActor(
                               hostActor: ActorRef,
                               itSupportActor: ActorRef,
                               securityActor: ActorRef
                             ) extends Actor with ActorLogging {

  import ServiceCoordinatorActor._
  import HostEmployeeActor._
  import ITSupportActor._
  import SecurityActor._

  override def receive: Receive = {
    case ProcessVisitorEvent(eventJson) =>
      log.info(s"[Coordinator] Event JSON:\n${eventJson.prettyPrint}")

      val json = eventJson.asJsObject
      val eventType = json.fields("event").convertTo[String]

      val visitor     = json.fields("visitor").asJsObject
      val visitorName = visitor.fields.get("fullName").map(_.convertTo[String]).getOrElse("")
      val visitorEmail = visitor.fields.get("email").map(_.convertTo[String]).getOrElse("")
      val purpose      = visitor.fields.get("purpose").map(_.convertTo[String]).getOrElse("")

      val hostOpt = json.fields.get("host").map(_.asJsObject)
      val hostName  = hostOpt.flatMap(_.fields.get("name")).map(_.convertTo[String]).getOrElse("")
      val hostEmail = hostOpt.flatMap(_.fields.get("email")).map(_.convertTo[String]).getOrElse("")

      eventType match {
        case "VISITOR_CHECK_IN" =>
          log.info("[Coordinator] Dispatching VISITOR_CHECK_IN")

          hostActor ! NotifyHost(hostName, hostEmail, visitorName, purpose)
          itSupportActor ! SendWifiCredentials(visitorEmail)
          securityActor ! LogVisitorEntry(visitorName)

        case "VISITOR_CHECK_OUT" =>
          log.info("[Coordinator] Dispatching VISITOR_CHECK_OUT")

          securityActor ! LogVisitorExit(visitorName)

        case unknown =>
          log.warning(s"[Coordinator] Unknown event: $unknown")
      }
  }
}

object ServiceCoordinatorActor {
  case class ProcessVisitorEvent(eventJson: JsValue)

  def props(
             host: ActorRef,
             it: ActorRef,
             security: ActorRef
           ): Props = Props(new ServiceCoordinatorActor(host, it, security))
}
