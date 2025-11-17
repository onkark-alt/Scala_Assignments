class Counter(val value: Int) {

  def +(that: Counter): Int = this.value + that.value

  def +(that: Int): Int = this.value + that
}

object CounterDemo {
  def main(args: Array[String]): Unit = {
    val a = new Counter(5)
    val b = new Counter(7)

    println(a + b)   // should print 12
    println(a + 10)  // should print 15
  }
}
