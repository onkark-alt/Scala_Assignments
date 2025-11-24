object Functions {
  def add(x:Int, y:Int):Int = x+y
  def add(x:Int):Int = x+1
  def greet(name:String = "ONKAR"):Unit = println(name)
  def unitFunc():Unit = println("unit")

  def main(args:Array[String]):Unit = {
    println(add(2,3))
    println(add(5))
    greet()
    unitFunc()
  }
}