class Aux(val name:String, val age:Int){
  def this(name:String) = this(name,0)
}
object AuxDemo {
  def main(args:Array[String]):Unit = {
    val a = new Aux("Ritik")
    val b = new Aux("Deepraj",20)
    println(a.name)
    println(b.age)
  }
}