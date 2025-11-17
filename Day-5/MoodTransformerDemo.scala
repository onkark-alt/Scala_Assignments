object MoodTransformerDemo {

  def moodChanger(prefix: String): String => String = {

    word => s"$prefix-$word-$prefix"
  }

  def main(args: Array[String]): Unit = {
    val happyMood = moodChanger("happy")
    println(happyMood("day")) 

    val angryMood = moodChanger("angry")
    println(angryMood("crowd"))

    val chillMood = moodChanger("chill")
    println(chillMood("evening"))
  }
}
