class Num(val x:Int){
  def +(that:Num) = new Num(this.x + that.x)
}
object OperatorDemo {
  def main(args:Array[String]):Unit = {
    val a = new Num(5)
    val b = new Num(7)
    println((a + b).x)
  }
}