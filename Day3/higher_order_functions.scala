//Higher-Order Functions Example 
object HigherOrderFunctionExample {

  // Higher-order function: takes a function as parameter
  def operateOnNumbers(a: Int, b: Int, operation: (Int, Int) => Int): Int = {
    operation(a, b)   // call the passed function
  }

  // Normal functions to pass into above
  def add(x: Int, y: Int): Int = x + y
  def multiply(x: Int, y: Int): Int = x * y

  def main(args: Array[String]): Unit = {

    val sumResult = operateOnNumbers(10, 5, add)
    println("Sum: " + sumResult) // Output: Sum: 15

    val productResult = operateOnNumbers(10, 5, multiply)
    println("Product: " + productResult) // Output: Product: 50

    // Passing anonymous function (lambda)
    val subtractResult = operateOnNumbers(10, 5, (x, y) => x - y)
    println("Subtract: " + subtractResult) // Output: Subtract: 5

    // Short lambda syntax
    val divideResult = operateOnNumbers(10, 5, _ / _)
    println("Divide: " + divideResult) // Output: Divide: 2
  }
}
