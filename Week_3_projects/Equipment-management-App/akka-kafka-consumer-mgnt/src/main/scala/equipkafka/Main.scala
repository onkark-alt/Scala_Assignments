package equipkafka.bootstrap

import akka.actor.typed.ActorSystem

object Main {
  def main(args: Array[String]): Unit = {
    println("Starting Equipment Kafka Consumer...")
    ActorSystem[RootCommand](RootBehavior(), "equipment-kafka-consumer-system")
  }
}
