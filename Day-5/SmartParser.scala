object SmartParser {
  // SsafeDivide function from Q3
  def safeDivide(x: Int, y: Int): Option[Int] =
    if (y == 0) None else Some(x / y)

  // parse input and divide
  def parseAndDivide(input: String): Either[String, Int] = {
    input.toIntOption match {
      case None => Left("Invalid number")           
      case Some(n) =>
        safeDivide(100, n) match {
          case None    => Left("Division by zero") 
          case Some(r) => Right(r)                 
        }
    }
  }

  def main(args: Array[String]): Unit = {
    println(parseAndDivide("25")) 
    println(parseAndDivide("0"))  
    println(parseAndDivide("abc"))
  }
}
