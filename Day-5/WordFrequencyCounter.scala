import scala.collection.MapView
object WordFrequencyCounter {

  def wordCount(lines: List[String]): MapView[String, Int] = {
    // Step 1 & 2 & 3: Flatten all words using for comprehension
    val words = for {
      line <- lines
      word <- line.split(" ")
    } yield word

    // Step 4 & 5: Group and count
    val frequency = words.groupBy(identity).mapValues(_.size)

    frequency
  }

  def main(args: Array[String]): Unit = {
    val lines = List(
      "Scala is powerful",
      "Scala is concise",
      "Functional programming is powerful"
    )

    val result = wordCount(lines)
    println(result)
  }
}
