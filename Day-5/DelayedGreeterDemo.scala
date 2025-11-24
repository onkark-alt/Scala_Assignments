object DelayedGreeterDemo {

  def delayedMessage(delayMs: Int)(message: String): Unit = {
    Thread.sleep(delayMs)  
    println(message)       
  }

  def main(args: Array[String]): Unit = {
    
    val oneSecondSay = delayedMessage(1000)

    oneSecondSay("Hello!")  
    oneSecondSay("How are you?")
    oneSecondSay("Scala is fun!")
    oneSecondSay("Goodbye!")

    println("All messages printed with 1-second delay between them.")
  }
}
