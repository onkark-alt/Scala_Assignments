package actors

import akka.actor.{Actor, ActorLogging, Props}
import utils.EmailHelper

class ITSupportActor extends Actor with ActorLogging {

  import ITSupportActor._

  override def receive: Receive = {
    case SendWifiCredentials(visitorEmail) =>
      log.info(s"[ITSupport] Sending WiFi credentials to $visitorEmail")

      val subject = "Temporary WiFi Access"
      val body =
        s"""
           |Your WiFi credentials:
           |Username: temp_visitor
           |Password: welcome123
           |""".stripMargin

      EmailHelper.sendEmail(visitorEmail, subject, body)
  }
}

object ITSupportActor {
  case class SendWifiCredentials(visitorEmail: String)

  def props(): Props = Props(new ITSupportActor())
}
