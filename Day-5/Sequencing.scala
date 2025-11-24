// Futures_Q1_Sequencing.scala
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
object FuturesSequencing extends App {
  def task(name: String, delay: Int): Future[String] = Future {
    Thread.sleep(delay)
    s"$name done"
  }

  // Sequential: chain using flatMap
  val t0 = System.nanoTime()
  val seq = task("A", 800).flatMap(_ => task("B", 800)).flatMap(_ => task("C", 800))
  val seqResult = Await.result(seq, 5.seconds)
  val t1 = System.nanoTime()
  val seqTimeMs = (t1 - t0) / 1000000
  println(s"Sequential result: $seqResult, time = ${seqTimeMs}ms")

  // Parallel: start all then sequence
  val t2 = System.nanoTime()
  val f1 = task("A", 800)
  val f2 = task("B", 800)
  val f3 = task("C", 800)
  val par = Future.sequence(List(f1, f2, f3)).map(_.mkString(", "))
  val parResult = Await.result(par, 5.seconds)
  val t3 = System.nanoTime()
  val parTimeMs = (t3 - t2) / 1000000
  println(s"Parallel result: $parResult, time = ${parTimeMs}ms")
}