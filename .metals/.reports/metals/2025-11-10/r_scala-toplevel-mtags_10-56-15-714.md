error id: file://<WORKSPACE>/Day5/SmartTemperature.scala:[8..9) in Input.VirtualFile("file://<WORKSPACE>/Day5/SmartTemperature.scala", "object  {

    def convertToFahrenheit(value: Double, scale:String): Double = {
        if(scale == "C") {
            value * 9.0 / 5.0 + 32.0
        } else if(scale == "K") {
            (value - 32.0) * 5.0 / 9.0
        } else {
            throw new IllegalArgumentException("Unsupported temperature scale")
        }
    }

    def main(args: Array[String]): Unit = {
        println(convertToFahrenheit(0, "C"))     // 32.0
        println(convertToFahrenheit(212, "F")) // 32.0
        println(convertToFahrenheit(50, "x"))   // 212.0
    }
}


")
file://<WORKSPACE>/file:<WORKSPACE>/Day5/SmartTemperature.scala
file://<WORKSPACE>/Day5/SmartTemperature.scala:1: error: expected identifier; obtained lbrace
object  {
        ^
#### Short summary: 

expected identifier; obtained lbrace