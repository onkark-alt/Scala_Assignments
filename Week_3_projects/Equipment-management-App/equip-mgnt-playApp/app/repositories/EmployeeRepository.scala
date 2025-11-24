package repositories

import models.{Employee, EmployeesTable}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

@Singleton
class EmployeeRepository @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider
                                  )(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val employees = EmployeesTable.table

  def insert(employee: Employee): Future[Long] =
    db.run((employees returning employees.map(_.id)) += employee)

  def findAll(): Future[Seq[Employee]] =
    db.run(employees.result)

  def findById(id: Long): Future[Option[Employee]] =
    db.run(employees.filter(_.id === id).result.headOption)

  def update(id: Long, emp: Employee): Future[Boolean] = {
    val query =
      employees
        .filter(_.id === id)
        .update(emp.copy(id = Some(id)))

    db.run(query).map(_ > 0)
  }

  def delete(id: Long): Future[Boolean] =
    db.run(employees.filter(_.id === id).delete).map(_ > 0)
}
