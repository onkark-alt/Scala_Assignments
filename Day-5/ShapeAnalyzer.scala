import scala.math.Pi

case class Circle(r: Double)
case class Rectangle(w: Double, h: Double)

object ShapeAnalyzer {
  def area(shape: Any): Double = shape match {
    case Circle(radius)        => Pi * radius * radius
    case Rectangle(width, height) => width * height
    case _                     => -1.0 
  }

  def main(args: Array[String]): Unit = {
    val c = Circle(3)
    val r = Rectangle(4, 5)

    println(f"Area of $c is ${area(c)}%.2f")
    println(f"Area of $r is ${area(r)}%.2f") 

    println(area("triangle")) 
  }
}
