object StringDSL:

  implicit class RichString(val s: String) extends AnyVal {

    def *(n: Int): String = s * n

    def ~(other: String): String = s + " " + other
  }

// Usage
@main def runStringDSL(): Unit =
  import StringDSL.*  

  println("Hi" * 3)           
  println("Hello" ~ "World")  
