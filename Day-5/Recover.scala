// Futures_Q2_Recover.scala
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
object FuturesRecover extends App {
  def riskyOperation(): Future[Int] = Future {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Failed!")
    n
  }

  // 1. recover to fallback -1
  val f1 = riskyOperation().recover { case _: Throwable => -1 }
  println("recover result: " + Await.result(f1, 2.seconds))

  // 2. recoverWith to retry once
  val f2 = riskyOperation().recoverWith { case _: Throwable => riskyOperation() }
  println("recoverWith (retry once) result: " + Await.result(f2, 2.seconds))
}