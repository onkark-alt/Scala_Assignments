object DepartmentEmployees {

  def flattenDepartments(departments: List[(String, List[String])]): List[String] = {
    val result = for {
      (dept, employees) <- departments   
      emp <- employees                   
    } yield s"$dept: $emp"               

    result
  }

  def main(args: Array[String]): Unit = {
    val departments = List(
      ("IT", List("Ravi", "Meena")),
      ("HR", List("Anita")),
      ("Finance", List("Vijay", "Kiran"))
    )

    val flatList = flattenDepartments(departments)
    flatList.foreach(println)
  }
}
