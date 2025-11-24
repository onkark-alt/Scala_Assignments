//Generic function example
object GenericExample {
  // Generic function that can take any type T
  def printData [T](item: T): Unit = {
    println(s"The item is: $item")
  }

  // Main method - entry point
  def main(args: Array[String]): Unit = {
    printData(25)                  // T = Int
    printData("Ritik")    // T = String
    printData(3.14)                // T = Double
    printData(List(1, 2, 3))       // T = List[Int]
  }
}