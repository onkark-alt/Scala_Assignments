package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime
import java.sql.Timestamp
case class Employee(
                     id: Option[Long],
                     name: String,
                     department: String,
                     email: String
                   )

class EmployeesTable(tag: Tag) extends Table[Employee](tag, "employees")
{


  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def department = column[String]("department")
  def email = column[String]("email")

  def * = (id.?, name, department, email) <> ((Employee.apply _).tupled, Employee.unapply)
}

object EmployeesTable {
  val table = TableQuery[EmployeesTable]
}
