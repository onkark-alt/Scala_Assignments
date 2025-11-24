case class Money(amount: Double) {

  def +(that: Money)(implicit precision: Double): Money = {
    val sum = this.amount + that.amount
    Money(Money.roundToPrecision(sum, precision))
  }
  def -(that: Money)(implicit precision: Double): Money = {
    val diff = this.amount - that.amount
    Money(Money.roundToPrecision(diff, precision))
  }

  override def toString: String = f"Money($amount%.2f)"
}

object Money {
  def roundToPrecision(value: Double, precision: Double): Double = {
    (Math.round(value / precision) * precision)
  }

  def main(args: Array[String]): Unit = {
    implicit val roundingPrecision: Double = 0.05

    val m1 = Money(10.23)
    val m2 = Money(5.19)

    println(m1 + m2) 
    println(m1 - m2) 

    {
      implicit val roundingPrecision: Double = 0.10
      println(m1 + m2) 
    }
  }
}
