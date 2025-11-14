object ControlStructures {
  def main(args:Array[String]):Unit = {
    for(i <- 1 to 5) println(i)
       println("---------------")
        for(i <- 1 until 5) println(i)
       println("---------------")

    var j = 0
    while(j < 3) { println(j); j += 1 }
  }
}
