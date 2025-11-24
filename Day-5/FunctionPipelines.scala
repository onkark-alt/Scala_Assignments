// Q2_FunctionPipelines.scala
object FunctionPipelines extends App {
  val trim: String => String = _.trim
  val toInt: String => Int = _.toInt
  val doubleIt: Int => Int = _ * 2

  // Using andThen: executes left-to-right
  val pipelineAndThen: String => Int = trim andThen toInt andThen doubleIt

  // Using compose: right-to-left
  val pipelineCompose: String => Int = doubleIt compose toInt compose trim

  println("\" 21 \" -> using andThen -> " + pipelineAndThen(" 21 "))    // 42
  println("\" 21 \" -> using compose  -> " + pipelineCompose(" 21 "))    // 42

  // Show swapping order in compose: compose changes evaluation flow
  val wrongCompose = trim compose toInt // type mismatch if used directly; demonstrate with types:
  // To show effect, create two simple functions
  val f: Int => Int = _ + 1
  val g: Int => Int = _ * 2
  val fComposeG = f compose g  // f(g(x)) => (x*2)+1
  val fAndThenG = f andThen g  // g(f(x)) => (x+1)*2
  println("f compose g on 3 => " + fComposeG(3))  // 7
  println("f andThen g on 3 => " + fAndThenG(3))  // 8
}