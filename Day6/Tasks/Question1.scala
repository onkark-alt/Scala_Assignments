abstract class Spacecraft(val fuelLevel: Double) {
    def launch():Unit
    def land():Unit = {
        println(s"[$this.getClass.getSimpleName] Landing sequence initiated.... Safe landing commited")
    }
}

trait Autopilot {
    def autoNavigate():Unit = {
        println("[Autopilot] Engaging automatic navigation system...")
    }
}

class CargoShip(fuel: Double) extends Spacecraft(fuel) with Autopilot {
    override def launch():Unit = {
        if (fuelLevel > 50) {
            println(s"[Cargoship] Launch sequence initiated.... Liftoff successful!")
        } else {
            println(s"[Cargoship] Insufficient fuel for launch.")
        }
    }
    final override def land(): Unit = {
        println(s"[Cargoship] Preparing for landing with heavy cargo...")
        super.land()
    }
}

class PassengerShip(fuel: Double) extends Spacecraft(fuel) with Autopilot {
    override def launch():Unit = {
        if (fuelLevel > 30) {
            println(s"[PassengerShip] Launch sequence initiated.... Liftoff successful!")
        } else {
            println(s"[PassengerShip] Insufficient fuel for launch.")
        }
    }
    final override def land(): Unit = {
        println(s"[PassengerShip] Preparing for landing with passengers onboard...")  
    }
}

final class LuxuryCruiser(fuel: Double) extends PassengerShip(fuel) {
    def playEntertainment(): Unit = {
    println("[LuxuryCruiser] Playing intergalactic movies and relaxing music for passengers.")
    }
}

object IntergalacticTransportSystem extends App {

  val cargo = new CargoShip(5000)
  val passenger = new PassengerShip(3000)
  val luxury = new LuxuryCruiser(8000)

  println("\n=== CargoShip Demo ===")
  cargo.launch()
  cargo.land()
  cargo.autoNavigate()

  println("\n=== PassengerShip Demo ===")
  passenger.launch()
  passenger.land()
  passenger.autoNavigate()

  println("\n=== LuxuryCruiser Demo ===")
  luxury.launch()
  luxury.land()
  luxury.autoNavigate()
  luxury.playEntertainment()
}

