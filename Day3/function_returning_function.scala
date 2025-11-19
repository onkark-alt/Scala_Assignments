object FunctionReturningExample {
  def greet(language: String): String => String = {
    if (language == "english") {
      (name: String) => s"Hello, $name"
    } else if (language == "marathi") {
      (name: String) => s"Namaskar, $name"
    } else {
      (name: String) => s"Hi, $name"
    }
  }

  def main(args: Array[String]): Unit = {
    val englishGreeting = greet("english")
    println(englishGreeting("Ritik"))   // Output: Hello, Ritik

    val spanishGreeting = greet("marathi")
    println(spanishGreeting("Deepraj"))  // Output: Namaskar, Deepraj

    println(greet("any")("User"))       // Output: Hi, User
  }
}