object IntentionalCrasherDemo {

  def main(args: Array[String]): Unit = {

    val safeDivide: PartialFunction[Int, String] = {
      case x if x != 0 => s"Result: ${100 / x}"
    }

    val safe = safeDivide.lift 

    println(safe(10))  
    println(safe(0))   

    safe(0) match {
      case Some(result) => println(result)
      case None => println("Cannot divide by zero!")
    }
  }
}
