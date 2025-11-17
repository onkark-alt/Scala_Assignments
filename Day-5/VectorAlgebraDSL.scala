case class Vec2D(x: Double, y: Double) {

  // Operator overloading
  def +(that: Vec2D): Vec2D = Vec2D(this.x + that.x, this.y + that.y)
  def -(that: Vec2D): Vec2D = Vec2D(this.x - that.x, this.y - that.y)
  def *(scalar: Double): Vec2D = Vec2D(this.x * scalar, this.y * scalar)

  // Custom toString for pretty output
  override def toString: String = s"Vec2D($x, $y)"
}

// Companion object to provide implicit conversion
object Vec2D {
  // Allow scalar * vector (like 3 * v)
  implicit class ScalarOps(val scalar: Double) extends AnyVal {
    def *(v: Vec2D): Vec2D = v * scalar
  }
}

object VectorAlgebraDSL {
  def main(args: Array[String]): Unit = {
    val v1 = Vec2D(2, 3)
    val v2 = Vec2D(4, 1)

    println(v1 + v2)  // Vec2D(6.0, 4.0)
    println(v1 - v2)  // Vec2D(-2.0, 2.0)
    println(v1 * 3)   // Vec2D(6.0, 9.0)
    println(3 * v1)   // Vec2D(6.0, 9.0)
  }
}
