object MethodExamples {
  def calc(a:Int):Int = a
  def calc(a:Int,b:Int):Int = a+b
  def show(a:Int,b:Int=5):Int = a+b
  def main(args:Array[String]):Unit = {
    println(calc(3))
    println(calc(3,4))
    println(show(3))
  }
}
