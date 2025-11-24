object MysteryOfPlusOperators {
  def main(args: Array[String]): Unit = {
    val nums = List(2, 4, 6)

    // 1. Add 8 to the end of the list
    val endAdded = nums :+ 8

    // 2. Add 0 to the beginning of the list
    val startAdded = 0 +: nums

    // 3. Build a new list [0, 2, 4, 6, 8] using both operators
    val finalList = 0 +: (nums :+ 8)

    println(s"Original list: $nums")
    println(s"Add 8 to end: $endAdded")
    println(s"Add 0 to start: $startAdded")
    println(s"Final new list: $finalList")
  }
}
