// Q5_MapFlatMapPipeline.scala
import scala.util.Try
object MapFlatMapPipeline extends App {
  val data = List("10", "20", "x", "30")

  val result = data
    .map(s => Try(s.toInt))    // List[Try[Int]]
    .collect { case scala.util.Success(n) => n } // filter successes
    .map(n => n * n)

  println(result) // List(100, 400, 900)
}