package services

import models.Employee
import repositories.EmployeeRepository
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmployeeService @Inject()(
                                 employeeRepo: EmployeeRepository
                               )(implicit ec: ExecutionContext) {

  def createEmployee(emp: Employee): Future[Long] =
    employeeRepo.insert(emp)

  def getEmployee(id: Long): Future[Option[Employee]] =
    employeeRepo.findById(id)

  def getAllEmployees: Future[Seq[Employee]] =
    employeeRepo.findAll()

  def updateEmployee(id: Long, emp: Employee): Future[Boolean] =
    employeeRepo.update(id, emp)

  def deleteEmployee(id: Long): Future[Boolean] =
    employeeRepo.delete(id)
}
