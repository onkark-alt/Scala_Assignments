object KeywordAndVarargs {
  def greet(name:String, age:Int=0):String = s"$name $age"
  def sum(nums:Int*):Int = nums.sum
  def main(args:Array[String]):Unit = {
    println(greet(age=25,name="Ritik"))
    println(sum(1,2,3,4))
  }
}
