package bootstrap

import akka.actor.{Actor, ActorLogging, Props}
import actors._
import kafka.KafkaVisitorConsumer

class RootActor extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("RootActor started")

    val hostActor     = context.actorOf(HostEmployeeActor.props(), "HostEmployeeActor")
    val itSupport     = context.actorOf(ITSupportActor.props(), "ITSupportActor")
    val securityActor = context.actorOf(SecurityActor.props(), "SecurityActor")

    val coordinator =
      context.actorOf(ServiceCoordinatorActor.props(hostActor, itSupport, securityActor), "ServiceCoordinator")

    KafkaVisitorConsumer.run(coordinator)(context.system)
  }

  override def receive: Receive = Actor.emptyBehavior
}

object RootActor {
  def props(): Props = Props(new RootActor())
}
