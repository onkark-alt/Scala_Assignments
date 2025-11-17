import scala.annotation.tailrec

object MaxInArrayTailRec {

  def maxInArray(arr: Array[Int]): Int = {

    @tailrec
    def loop(i: Int, currentMax: Int): Int = {
      if (i == arr.length) {
        currentMax                    // Base case: reached end of array
      } else {
        val newMax = if (arr(i) > currentMax) arr(i) else currentMax
        loop(i + 1, newMax)           // Recursive call with next index
      }
    }

    if (arr.isEmpty) {
      throw new IllegalArgumentException("Array is empty")
    } else {
      loop(1, arr(0))                 // Start with index 1, currentMax = first element
    }
  }

  def main(args: Array[String]): Unit = {
    val nums = Array(5, 9, 3, 7, 2)
    println(s"Max element: ${maxInArray(nums)}")  // Output: 9
  }
}
