error id: file://<WORKSPACE>/Day5/SmartTemperature.scala:[9..12) in Input.VirtualFile("file://<WORKSPACE>/Day5/SmartTemperature.scala", "object 

def convertToFahrenheit(value: Double, scale:String): Double = {
    if(scale == "C") {
        value * 9.0 / 5.0 + 32.0
    } else if(scale == "K") {
        (value - 32.0) * 5.0 / 9.0
    } else {
        throw new IllegalArgumentException("Unsupported temperature scale")
    }
}


object SmartTempConverter {

  // Function to convert temperature
  def convertTemp(value: Double, scale: String): Double = {
    if (scale == "C") {
      // Convert Celsius to Fahrenheit
      value * 9.0 / 5.0 + 32.0
    } else if (scale == "F") {
      // Convert Fahrenheit to Celsius
      (value - 32.0) * 5.0 / 9.0
    } else {
      // Invalid scale, return value unchanged
      value
    }
  }

  // Main function
  def main(args: Array[String]): Unit = {
    println(convertTemp(0, "C"))     // 32.0
    println(convertTemp(212, "F"))   // 100.0
    println(convertTemp(50, "X"))    // 50.0
  }
}
")
file://<WORKSPACE>/file:<WORKSPACE>/Day5/SmartTemperature.scala
file://<WORKSPACE>/Day5/SmartTemperature.scala:3: error: expected identifier; obtained def
def convertToFahrenheit(value: Double, scale:String): Double = {
^
#### Short summary: 

expected identifier; obtained def