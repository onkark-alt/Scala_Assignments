import akka.actor.typed.ActorSystem
import actors.Supervisor

object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem(Supervisor(), "SensorSimulationSystem")
  }
}
