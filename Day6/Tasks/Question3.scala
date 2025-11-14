trait Robot {
  def start(): Unit
  def shutdown(): Unit
  def status(): Unit = println("Robot is operational")
}

trait SpeechModule {
  def speak(message: String): Unit = println(s"Robot says: $message")
}

trait MovementModule {
  def moveForward(): Unit = println("Moving forward")
  def moveBackward(): Unit = println("Moving backward")
}

trait EnergySaver {
  def activateEnergySaver(): Unit = println("Energy saver mode activated")
  
  // Fix: explicitly override
  override def shutdown(): Unit = println("Robot shutting down to save energy")
}

class BasicRobot extends Robot {
  override def start(): Unit = println("BasicRobot starting up")
  override def shutdown(): Unit = println("BasicRobot shutting down")
}

object RobotTest extends App {

  println("\n=== Robot1: Speech + Movement ===")
  val robot1 = new BasicRobot with SpeechModule with MovementModule
  robot1.start()             
  robot1.status()            
  robot1.speak("Hello!")     
  robot1.moveForward()       
  robot1.shutdown()          

  println("\n=== Robot2: EnergySaver + Movement ===")
  val robot2 = new BasicRobot with EnergySaver with MovementModule
  robot2.start()                 
  robot2.moveBackward()          
  robot2.activateEnergySaver()   
  robot2.shutdown()              

  println("\n=== Robot3: Movement + EnergySaver ===")
  val robot3 = new BasicRobot with MovementModule with EnergySaver
  robot3.start()                 
  robot3.moveForward()           
  robot3.activateEnergySaver()   
  robot3.shutdown()              

  println("\n=== Robot4: Speech + Movement + EnergySaver ===")
  val robot4 = new BasicRobot with SpeechModule with MovementModule with EnergySaver
  robot4.start()
  robot4.speak("I am fully operational")
  robot4.moveForward()
  robot4.activateEnergySaver()
  robot4.shutdown()              
}
