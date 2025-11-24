package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._
import services.EquipmentService
import models.Equipment
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentController @Inject()(
                                     cc: ControllerComponents,
                                     equipmentService: EquipmentService
                                   )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val eqFormat = Json.format[Equipment]

  // Add Equipment
  def addEquipment: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[Equipment].fold(
        errors => Future.successful(BadRequest(JsError.toJson(errors))),
        eq => {
          equipmentService.addEquipment(eq.copy(id = None)).map { id =>
            Ok(Json.obj("status" -> "success", "id" -> id))
          }
        }
      )
    }

  // List Equipment
  def listEquipment: Action[AnyContent] =
    Action.async {
      equipmentService.listEquipment().map(eq => Ok(Json.toJson(eq)))
    }

  // Get Equipment by ID
  def getEquipment(id: Long): Action[AnyContent] =
    Action.async {
      equipmentService.getEquipment(id).map {
        case Some(eq) => Ok(Json.toJson(eq))
        case None     => NotFound(Json.obj("error" -> "Equipment not found"))
      }
    }

  // Update Equipment Status
  def updateEquipmentStatus(id: Long): Action[JsValue] =
    Action.async(parse.json) { request =>

      val statusResult = (request.body \ "status").validate[String]
      val notesResult  = (request.body \ "notes").validateOpt[String]

      (statusResult, notesResult) match {
        case (JsSuccess(newStatus, _), JsSuccess(notes, _)) =>
          equipmentService.updateEquipmentStatus(id, newStatus, notes).map {
            case 1 =>
              Ok(Json.obj(
                "status" -> "updated",
                "id" -> id,
                "newStatus" -> newStatus,
                "notes" -> notes
              ))
            case _ =>
              NotFound(Json.obj("error" -> "Equipment not found"))
          }

        case _ =>
          Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON format")))
      }
    }
}
