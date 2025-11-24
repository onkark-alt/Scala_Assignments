object CombineLists {

  def validPairs(students: List[String], subjects: List[String]): List[(String, String)] = {
    for {
      student <- students
      subject <- subjects
      if student.length >= subject.length
    } yield (student, subject)
  }

  def main(args: Array[String]): Unit = {
    val students = List("Asha", "Bala", "Chitra")
    val subjects = List("Math", "Physics")

    val result = validPairs(students, subjects)
    println(result)
  }
}

