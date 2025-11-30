package actors

import akka.actor.{Actor, ActorLogging, Props}
import utils.EmailHelper

class HostEmployeeActor extends Actor with ActorLogging {

  import HostEmployeeActor._

  override def receive: Receive = {
    case NotifyHost(hostName, hostEmail, visitorName, purpose) =>
      log.info(s"[HostActor] Email to host $hostEmail")

      val subject = "Visitor Arrival Notification"
      val body =
        s"""
           |Hi $hostName,
           |
           |Your visitor $visitorName has arrived at reception for $purpose
           |""".stripMargin

      EmailHelper.sendEmail(hostEmail, subject, body)
  }
}

object HostEmployeeActor {
  case class NotifyHost(hostName: String, hostEmail: String, visitorName: String, purpose: String)

  def props(): Props = Props(new HostEmployeeActor())
}
