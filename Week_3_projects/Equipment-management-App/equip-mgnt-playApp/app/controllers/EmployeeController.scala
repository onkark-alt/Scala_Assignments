package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._
import services.EmployeeService
import models.Employee
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(
                                    cc: ControllerComponents,
                                    employeeService: EmployeeService
                                  )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val employeeFormat = Json.format[Employee]

  // CREATE EMPLOYEE
  def createEmployee: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[Employee].fold(
        errors => Future.successful(BadRequest(JsError.toJson(errors))),
        employee =>
          employeeService.createEmployee(employee).map { id =>
            Ok(Json.obj("status" -> "success", "id" -> id))
          }
      )
    }

  // GET EMPLOYEE BY ID
  def getEmployee(id: Long): Action[AnyContent] =
    Action.async {
      employeeService.getEmployee(id).map {
        case Some(emp) => Ok(Json.toJson(emp))
        case None      => NotFound(Json.obj("error" -> "Employee not found"))
      }
    }

  // GET ALL EMPLOYEES
  def getAllEmployees: Action[AnyContent] =
    Action.async {
      employeeService.getAllEmployees.map { list =>
        Ok(Json.toJson(list))
      }
    }

  // UPDATE EMPLOYEE
  def updateEmployee(id: Long): Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[Employee].fold(
        errors => Future.successful(BadRequest(JsError.toJson(errors))),
        emp =>
          employeeService.updateEmployee(id, emp).map { updated =>
            if (updated) Ok(Json.obj("status" -> "updated"))
            else NotFound(Json.obj("error" -> "Employee not found"))
          }
      )
    }

  // DELETE EMPLOYEE
  def deleteEmployee(id: Long): Action[AnyContent] =
    Action.async {
      employeeService.deleteEmployee(id).map { deleted =>
        if (deleted) Ok(Json.obj("status" -> "deleted"))
        else NotFound(Json.obj("error" -> "Employee not found"))
      }
    }
}
