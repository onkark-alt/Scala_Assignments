//Curry function example 
object CurryingExample {

  // Normal function (takes 2 parameters)
  def addNormal(a: Int, b: Int): Int = a + b

  // Curried function (takes parameters in multiple lists)
  def addCurried(a: Int)(b: Int): Int = a + b

  def main(args: Array[String]): Unit = {
    println(addNormal(10, 5))   // Output: 15

    println(addCurried(10)(5))  // Output: 15
  }
}