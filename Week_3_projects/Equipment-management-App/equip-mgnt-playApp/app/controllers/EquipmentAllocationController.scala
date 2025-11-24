package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

import services.{EquipmentAllocationService, KafkaProducerService}
import models.EquipmentAllocation

@Singleton
class EquipmentAllocationController @Inject()(
                                               cc: ControllerComponents,
                                               allocationService: EquipmentAllocationService,
                                               kafkaProducer: KafkaProducerService
                                             )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val allocFormat = Json.format[EquipmentAllocation]

  // Allocate Equipment
  def allocate: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[EquipmentAllocation].fold(
        errors => Future.successful(BadRequest(JsError.toJson(errors))),
        alloc =>
          allocationService.allocate(alloc.copy(id = None)).flatMap { id =>
            val eventJson = Json.obj(
              "event" -> "allocated",
              "allocationId" -> id,
              "equipmentId" -> alloc.equipmentId,
              "employeeId" -> alloc.employeeId
            )

            println(eventJson)
            kafkaProducer.send("equipment.allocated", eventJson.toString()).map { _ =>
              Ok(Json.obj("status" -> "allocated", "id" -> id))
            }
          }
      )
    }

  // Get Allocation by ID
  def getAllocation(id: Long): Action[AnyContent] =
    Action.async {
      allocationService.getAllocation(id).map {
        case Some(alloc) => Ok(Json.toJson(alloc))
        case None        => NotFound(Json.obj("error" -> "Allocation not found"))
      }
    }

  // Mark Returned
  def markReturned(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
      val returnedCondition = (request.body \ "returnedCondition").as[String]


      allocationService.markReturned(id,returnedCondition).flatMap {
        case 1 =>
          val eventJson = Json.obj(
            "event" -> "returned",
            "allocationId" -> id,
            "returnedCondition" -> returnedCondition
          )

          kafkaProducer.send("equipment.returned", eventJson.toString()).map { _ =>
            Ok(Json.obj("status" -> "returned", "id" -> id))
          }

        case _ =>
          Future.successful(NotFound(Json.obj("error" -> "Allocation not found")))
      }
    }

  // Overdue Allocations
  def overdue: Action[AnyContent] =
    Action.async {
      allocationService.findOverdue().map { list =>
        Ok(Json.toJson(list))
      }
    }
}
