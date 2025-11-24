object SmartTemperature {

  def convertTemp(value: Double, scale: String): Double = {
    if (scale == "C") {
      value * 9.0 / 5.0 + 32.0
    } else if (scale == "F") {
      (value - 32.0) * 5.0 / 9.0
    } else {
      value
    }
  }

  def main(args: Array[String]): Unit = {
    println(convertTemp(0, "C"))     // 32.0
    println(convertTemp(212, "F"))   // 100.0
    println(convertTemp(50, "X"))    // 50.0
  }
}



