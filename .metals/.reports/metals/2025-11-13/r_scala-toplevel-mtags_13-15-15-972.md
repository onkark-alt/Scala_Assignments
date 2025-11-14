error id: file://<WORKSPACE>/Day7/Tasks/Question1.scala:[1281..1281) in Input.VirtualFile("file://<WORKSPACE>/Day7/Tasks/Question1.scala", "abstract class Spacecraft(val fuelLevel: Double) {
    def launch():Unit
    def land():Unit = {
        println(s"[$this.getClass.getSimpleName] Landing sequence initiated.... Safe landing commited")
    }
}

trait Autopilot {
    def autonavigate():Unit = {
        println("[Autopilot] Engaging automatic navigation system...")
    }
}

class Cargoship(fuel: Double) extends Spacecraft(fuel) with Autopilot {
    override def launch():Unit = {
        if (fuelLevel > 50) {
            println(s"[Cargoship] Launch sequence initiated.... Liftoff successful!")
        } else {
            println(s"[Cargoship] Insufficient fuel for launch.")
        }
    }
    override def land(): Unit = {
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

final class")
file://<WORKSPACE>/file:<WORKSPACE>/Day7/Tasks/Question1.scala
file://<WORKSPACE>/Day7/Tasks/Question1.scala:41: error: expected identifier; obtained eof
final class
           ^
#### Short summary: 

expected identifier; obtained eof