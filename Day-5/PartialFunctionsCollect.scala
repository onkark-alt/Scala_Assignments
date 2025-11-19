// Q3_PartialFunctionsCollect.scala
object PartialFunctionsCollect extends App {
  val items: List[Any] = List(1, "apple", 3.5, "banana", 42)

  val doubledInts = items.collect { case i: Int => i * 2 }

  println(doubledInts) // List(2, 84)
}