object ArrayMirrorPuzzle {

  def mirrorArray(arr: Array[Int]): Array[Int] = {
    val n = arr.length
    val result = new Array[Int](n * 2)

    // Fill the first half with original elements
    for (i <- 0 until n) {
      result(i) = arr(i)
    }

    // Fill the second half with mirrored elements
    for (i <- 0 until n) {
      result(n + i) = arr(n - 1 - i)
    }

    result
  }

  def main(args: Array[String]): Unit = {
    val input = Array(1, 2, 3)
    val output = mirrorArray(input)
    println(output.mkString("Array(", ", ", ")"))
  }
}
