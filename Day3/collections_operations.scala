object CollectionsOperations {
  def main(args:Array[String]):Unit = {
    val xs = List(1,2,3,4,5)
    println(xs.map(_*2))
    println(xs.filter(_%2==0))
    println(xs.reduce(_+_))
    println(xs.foldLeft(0)(_+_))
    println(xs.foldRight(0)(_+_))
    println(xs.scanLeft(0)(_+_))
    println(xs.scanRight(0)(_+_))
    println(xs.collect{case x if x%2==1 => x*10})
  }
}