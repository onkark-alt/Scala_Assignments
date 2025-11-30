package equipkafka.bootstrap

import akka.actor.{ActorSystem, Props}
import equipkafka.actors.RootSupervisorActor

object Main {
  def main(args: Array[String]): Unit = {
    println("Starting Equipment Kafka Consumer...")

    val system = ActorSystem("equipment-kafka-consumer-system")
    system.actorOf(Props[RootSupervisorActor], "rootSupervisor")
  }
}
