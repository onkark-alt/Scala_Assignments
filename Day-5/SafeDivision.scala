// Q1_SafeDivision.scala
object SafeDivision extends App {
  def safeDivide(a: Int, b: Int): Either[String, Double] =
    if (b == 0) Left("Division by zero")
    else Right(a.toDouble / b.toDouble)

  val pairs = List((10, 2), (5, 0), (8, 4))
  val mapped = pairs.map { case (a,b) => safeDivide(a,b) }

  val valids = mapped.collect { case Right(v) => v }
  val errors = mapped.collect { case Left(e) => e }

  println(s"Valid results: ${valids} Errors: ${errors.map(e => "\"" + e + "\"")}")
}