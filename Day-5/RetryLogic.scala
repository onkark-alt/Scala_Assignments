// Q4_RetryLogic.scala
import scala.annotation.tailrec
object RetryLogic extends App {
  def fetchData(): Int = {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Network fail")
    n
  }

  @tailrec
  def retry[T](times: Int)(op: => T): Option[T] = {
    if (times <= 0) None
    else {
      try {
        Some(op)
      } catch {
        case _: Throwable => retry(times - 1)(op)
      }
    }
  }

  val data = retry(3)(fetchData())
  println(data)
}