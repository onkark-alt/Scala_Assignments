import scala.annotation.tailrec
object TailRecursiveFactorial{

    def factorial(n: Int): Int = {

        @tailrec
        def helper(n: Int, accumulator: Int): Int = {
            println(s"[acc=$accumulator, n=$n]")  
            if (n <= 1) {
                accumulator
            } else {
                helper(n - 1, n * accumulator)
            }
        }
        helper(n, 1)
    }

    def main(args: Array[String]): Unit = {
        val n = 5;
        println(s"Factorail of $n is ${factorial(n)}")  
    }
}