object MergeGameDemo {
  def main(args: Array[String]): Unit = {
    val a = List(1, 2)
    val b = List(3, 4)
    val v = Vector(5, 6)

    val c1 = a ++ b
    println(s"a ++ b = $c1") 

    val c2 = a ::: b
    println(s"a ::: b = $c2") 

    val combined = a ++ b ++ v
    println(s"Combined list + vector = $combined") 
  }
}
