class Normal(val name:String, val age:Int)
case class Person(name:String, age:Int)
object NormalVsCase {
  def main(args:Array[String]):Unit = {
    val a = new Normal("Ritik",25)
    val b = Person("Deepraj",20)
    val c=b.copy(name="Ritik")
    println(a.name)
    println(b)
    println(b==c)
  }
}
