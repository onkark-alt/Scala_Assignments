import akka.actor.ActorSystem
import bootstrap.RootActor

object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem("VisitorConsumerSystem").actorOf(RootActor.props(), "RootActor")
  }
}
