case class Person(name: String, age: Int)

// Define an implicit class to add comparison operators
object PersonOrdering {
  implicit class RichPerson(p1: Person) {
    def <(p2: Person): Boolean = p1.age < p2.age
    def >(p2: Person): Boolean = p1.age > p2.age
    def <=(p2: Person): Boolean = p1.age <= p2.age
    def >=(p2: Person): Boolean = p1.age >= p2.age
  }
}

object ImplicitOrderingExample {
  import PersonOrdering._ // Bring implicit class into scope

  def main(args: Array[String]): Unit = {
    val p1 = Person("Ravi", 25)
    val p2 = Person("Meena", 30)

    println(p1 < p2)   // true
    println(p1 >= p2)  // false
    println(p2 > p1)   // true
    println(p2 <= p1)  // false

    // Example use in a condition
    if (p1 < p2)
      println(s"${p2.name} is older than ${p1.name}")
  }
}
