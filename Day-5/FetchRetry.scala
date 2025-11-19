// Futures_Q5_FetchRetry.scala
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
object FuturesFetchRetry extends App {
  def fetchDataFromServer(server: String): Future[String] = Future {
    if (scala.util.Random.nextBoolean()) throw new RuntimeException(s"$server failed")
    s"$server: OK"
  }

  def fetchWithRetry(server: String, maxRetries: Int): Future[String] = {
    fetchDataFromServer(server).recoverWith {
      case _ if maxRetries > 0 => fetchWithRetry(server, maxRetries - 1)
    }
  }

  val f = fetchWithRetry("Server-1", 3)
  f.onComplete(println)
  // keep the JVM alive briefly for the demo
  Await.ready(f, 10.seconds)
}