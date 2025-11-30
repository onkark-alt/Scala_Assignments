package actors

import akka.actor.{Actor, ActorLogging, Props}

class SecurityActor extends Actor with ActorLogging {

  import SecurityActor._

  override def receive: Receive = {
    case LogVisitorEntry(visitorName) =>
      log.info(s"[Security] Visitor ENTERED: $visitorName")

    case LogVisitorExit(visitorName) =>
      log.info(s"[Security] Visitor EXITED: $visitorName")
  }
}

object SecurityActor {
  case class LogVisitorEntry(visitorName: String)
  case class LogVisitorExit(visitorName: String)

  def props(): Props = Props(new SecurityActor())
}
