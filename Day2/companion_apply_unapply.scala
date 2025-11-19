class Sample(val x:Int)
object Sample {
  def apply(x:Int) = new Sample(x)
  def unapply(s:Sample) = Some(s.x)
}
object DemoApplyUnapply {
  def main(args:Array[String]):Unit = {
    val s = Sample(10)
    val Sample(v) = s
    println(v)
  }
}