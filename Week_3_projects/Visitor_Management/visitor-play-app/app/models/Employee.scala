package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Employee(
                     id: Long = 0L,
                     fullName: String,
                     email: String,
                     phone: String,
                     departmentName: String,
                     createdAt: Option[Timestamp] = None,
                     updatedAt: Option[Timestamp] = None
                   )

class EmployeeDAO @Inject()(
                             dbConfigProvider: DatabaseConfigProvider
                           )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // ----------------------------------
  // Employee table
  // ----------------------------------
  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employees") {

    def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fullName     = column[String]("full_name")
    def email        = column[String]("email")
    def phone        = column[String]("phone")
    def departmentName = column[String]("department_name")
    def createdAt    = column[Option[Timestamp]]("created_at")
    def updatedAt    = column[Option[Timestamp]]("updated_at")

    def * =
      (id, fullName, email, phone, departmentName, createdAt, updatedAt)
        .<>(Employee.tupled, Employee.unapply)
  }

  val employees = TableQuery[EmployeeTable]

  // ----------------------------------
  // CRUD
  // ----------------------------------

  def list(): Future[Seq[Employee]] =
    db.run(employees.result)

  def getById(id: Long): Future[Option[Employee]] =
    db.run(employees.filter(_.id === id).result.headOption)

  def insert(e: Employee): Future[Employee] =
    db.run(
      (employees returning employees.map(_.id)
        into ((emp, id) => emp.copy(id = id))
        ) += e
    )
}
