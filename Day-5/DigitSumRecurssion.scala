object DigitSumRecursion {

  // Recursive function to compute sum of digits
  def digitSum(n: Int): Int = {
    if (n == 0) {
      0                              // Base case
    } else {
      (n % 10) + digitSum(n / 10)    // Recursive case
    }
  }

  def main(args: Array[String]): Unit = {
    println(digitSum(1345))   // 13
    println(digitSum(0))      // 0
    println(digitSum(9999))   // 36
  }
}
