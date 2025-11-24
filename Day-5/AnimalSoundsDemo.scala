object AnimalSoundsDemo {
  def main(args: Array[String]): Unit = {
    val animals = Map(
      "dog" -> "bark",
      "cat" -> "meow",
      "cow" -> "moo"
    )

    val updatedAnimals = animals + ("lion" -> "roar")

    val cowSound = updatedAnimals("cow")
    println(s"Cow says: $cowSound") 

    val tigerSound = updatedAnimals.getOrElse("tiger", "unknown")
    println(s"Tiger says: $tigerSound") 

    println(s"Updated animals map: $updatedAnimals")
  }
}
