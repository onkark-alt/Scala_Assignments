error id: file://<WORKSPACE>/Day5/TailRecursiveFactorial.scala:[352..353) in Input.VirtualFile("file://<WORKSPACE>/Day5/TailRecursiveFactorial.scala", "import scala.annotation.tailrec
object TailRecursiveFactorial{

    def factorial(n: Int): Int = {
        @tailrec
        def helper(n: Int, accumulator: Int): Int = {
            if (n <= 1) {
                accumulator
            } else {
                helper(n - 1, n * accumulator)
            }
        }
        helper(n, 1)
    }

    def
}")
file://<WORKSPACE>/file:<WORKSPACE>/Day5/TailRecursiveFactorial.scala
file://<WORKSPACE>/Day5/TailRecursiveFactorial.scala:17: error: expected identifier; obtained rbrace
}
^
#### Short summary: 

expected identifier; obtained rbrace