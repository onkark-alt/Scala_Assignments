trait Drone {
  def activate(): Unit
  def deactivate(): Unit
  def status(): Unit = println("Drone is operational")
}

trait NavigationModule {
  def flyTo(destination: String): Unit = println(s"Flying to $destination")
  def deactivate(): Unit = println("Navigation systems shutting down")
}

trait DefenseModule {
  def activateShields(): Unit = println("Shields activated")
  def deactivate(): Unit = println("Defense systems deactivated")
}

trait CommunicationModule {
  def sendMessage(msg: String): Unit = println(s"Sending message: $msg")
  def deactivate(): Unit = println("Communication module shutting down")
}

class BasicDrone extends Drone {
  override def activate(): Unit = println("BasicDrone activating")
  override def deactivate(): Unit = println("BasicDrone shutting down")
}

object DroneFleetTest extends App {

  println("\n=== Drone1: Navigation + Defense ===")
  val drone1 = new BasicDrone with NavigationModule with DefenseModule {
    override def deactivate(): Unit = super[DefenseModule].deactivate()
  }
  drone1.activate()
  drone1.status()
  drone1.flyTo("Mars")
  drone1.activateShields()
  drone1.deactivate() 

  println("\n=== Drone2: Communication + Navigation ===")
  val drone2 = new BasicDrone with CommunicationModule with NavigationModule {
    override def deactivate(): Unit = super[NavigationModule].deactivate()
  }
  drone2.activate()
  drone2.status()
  drone2.sendMessage("Hello Earth!")
  drone2.flyTo("Moon")
  drone2.deactivate() 

  println("\n=== Drone3: Defense + Communication + Navigation ===")
  val drone3 = new BasicDrone with DefenseModule with CommunicationModule with NavigationModule {
    override def deactivate(): Unit = super[NavigationModule].deactivate()
  }
  drone3.activate()
  drone3.status()
  drone3.activateShields()
  drone3.sendMessage("Mission Complete")
  drone3.flyTo("Jupiter")
  drone3.deactivate() 

  println("\n=== Drone4: Navigation + Communication + Defense ===")
  val drone4 = new BasicDrone with NavigationModule with CommunicationModule with DefenseModule {
    override def deactivate(): Unit = super[DefenseModule].deactivate()
  }
  drone4.activate()
  drone4.status()
  drone4.flyTo("Saturn")
  drone4.sendMessage("Ready for Battle")
  drone4.activateShields()
  drone4.deactivate()
}
