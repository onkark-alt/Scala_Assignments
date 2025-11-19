// Futures_Q3_Chaining.scala
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
object FuturesChaining extends App {
  def getUser(id: Int): Future[String] = Future { s"User$id" }
  def getOrders(user: String): Future[List[String]] = Future { List(s"$user-order1", s"$user-order2") }
  def getOrderTotal(order: String): Future[Double] = Future { scala.util.Random.between(10.0, 100.0) }

  val totalF = for {
    user <- getUser(42)
    orders <- getOrders(user)
    totals <- Future.sequence(orders.map(getOrderTotal))
  } yield totals.sum

  println("Total: " + Await.result(totalF, 5.seconds))
}