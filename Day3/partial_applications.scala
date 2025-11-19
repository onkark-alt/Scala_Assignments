//Partial Application Example 
object PartialApplicationExample {

  def multiply(x: Int)(y: Int): Int = x * y

  def main(args: Array[String]): Unit = {

    val times2 = multiply(2)  // Fixing x = 2 (partial function)
    println(times2(10))          // Output: 20

    val times10 = multiply(10)  // Fixing x = 10
    println(times10(5))          // Output: 50
  }
}