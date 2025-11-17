// Define the case classes
case class Address(city: String, pincode: Int)
case class Person(name: String, address: Address)

object UnapplyChainExample {

  def main(args: Array[String]): Unit = {

    val p = Person("Ravi", Address("Chennai", 600001))
    val q = Person("Meena", Address("Mumbai", 400001))
    val r = Person("Ajay", Address("Delhi", 110001))

    println("=== Basic Nested Pattern Matching ===")
    p match {
      case Person(_, Address(city, pin)) =>
        println(s"$city - $pin")
      case null => 
        println("Got null Person")
    }

    println("\n=== Pattern Guard Example ===")
    p match {
      case Person(_, Address(city, pin)) if city.startsWith("C") =>
        println(s"City starts with 'C' → $city - $pin")
      case Person(_, Address(city, _)) =>
        println(s"City does not start with 'C' → $city")
      case null =>
        println("Got null Person")
    }

    println("\n=== Multiple Pattern Guard Examples ===")
    for (person <- List(p, q, r)) {
      person match {
        case Person(_, Address(city, pin)) if city.startsWith("C") || city.startsWith("M") =>
          println(s"Preferred City → $city ($pin)")
        case Person(_, Address(city, _)) =>
          println(s"Other City → $city")
        case null =>
          println("Got null Person")
      }
    }
  }
}
