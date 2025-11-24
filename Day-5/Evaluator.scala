object Evaluator {

  def apply(block: => Any): Unit = {
    println("Evaluating block...")
    val result = block
    println(s"Result = $result")
  }
}

@main def testEvaluator(): Unit = {
  Evaluator {
    val x = 5
    val y = 3
    x * y + 2
  }
}
