object LazyExample {
  def main(args: Array[String]): Unit = {

    val normalVal = {
      println("Eager evaluation")
      5
    }

    lazy val lazyVal = {
      println("Lazy evaluation")
      10
    }

    def defVal = {
      println("Def evaluation (method call)")
      15
    }

    println("Program started")

    println("Accessing normalVal: " + normalVal)
    println("Accessing lazyVal: " + lazyVal)
    println("Accessing defVal: " + defVal)

    println("Accessing lazyVal again: " + lazyVal)
    println("Accessing defVal again: " + defVal)
  }
}
