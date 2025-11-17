object PersonalizedCalculator {

  def calculate(op: String)(x: Int, y: Int): Int = op.toLowerCase match {
    case "add" => x + y
    case "sub" => x - y
    case "mul" => x * y
    case "div" => x / y
    case _     => throw new IllegalArgumentException(s"Unknown operation: $op")
  }

  def main(args: Array[String]): Unit = {

    val add = calculate("add")
    val subtract = calculate("sub")
    val multiply = calculate("mul")
    val divide = calculate("div")

    println(add(10, 5))       
    println(subtract(10, 5))  
    println(multiply(3, 4))   
    println(divide(20, 4))    

    try {
      println(calculate("mod")(10, 3))
    } catch {
      case e: IllegalArgumentException => println(e.getMessage)
    }
  }
}
