trait Device {
    def turnOn(): Unit           // abstract
    def turnOff(): Unit          // abstract

    def status(): Unit = {
        println("Device is operational")
    }
}

trait Connectivity {
    def connect(): Unit = {
        println("Device connected to network")
    }
    def disconnect(): Unit = {
        println("Device disconnected")
    }
}

trait EnergySaver {
    def activateEnergySaver(): Unit = {
        println("Energy saver mode activated")
    }
    
    def turnOff(): Unit = {
        println("Device powered down to save energy")
    }
}

trait VoiceControl {
    def turnOn(): Unit = {
        println("Voice command: Turn on device")
    }
    def turnOff(): Unit = {
        println("Voice command: Turn off device")
    }
}

class SmartLight extends Device with Connectivity with EnergySaver {
    override def turnOn(): Unit = {
        println("SmartLight turned on")
    }
    override def turnOff(): Unit = {
        println("SmartLight turned off")
    }
}

class SmartThermostat extends Device with Connectivity {
    override def turnOn(): Unit = {
        println("SmartThermostat turned on")
    }
    override def turnOff(): Unit = {
        println("SmartThermostat turned off")
    }
}

object SmartHomeTest extends App {

  println("\n=== SmartLight Demo ===")
  val light = new SmartLight
  light.turnOn()               
  light.turnOff()              
  light.status()               
  light.connect()              
  light.activateEnergySaver()  

  println("\n=== SmartThermostat Demo ===")
  val thermostat = new SmartThermostat
  thermostat.turnOn()         
  thermostat.turnOff()    
  thermostat.status()       
  thermostat.connect()     

  println("\n=== Voice-Controlled SmartLight Demo ===")

  val voiceLight = new SmartLight with VoiceControl {
    override def turnOn(): Unit = {
        super[VoiceControl].turnOn()
        }
    override def turnOff(): Unit = {
        super[VoiceControl].turnOff()
    }
  }

  voiceLight.turnOn()           
  voiceLight.turnOff()         
  voiceLight.connect()        
  voiceLight.activateEnergySaver() 
}
