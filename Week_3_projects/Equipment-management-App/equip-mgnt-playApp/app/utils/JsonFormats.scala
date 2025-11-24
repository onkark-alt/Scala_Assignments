package utils

import play.api.libs.json._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import models._

object JsonFormats {

  // ------------------------------------------------------------
  // LocalDateTime formatter
  // ------------------------------------------------------------
  implicit val localDateTimeFormat: Format[LocalDateTime] = new Format[LocalDateTime] {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override def writes(dt: LocalDateTime): JsValue =
      JsString(dt.format(formatter))

    override def reads(json: JsValue): JsResult[LocalDateTime] =
      json.validate[String].map(LocalDateTime.parse(_, formatter))
  }

  // ------------------------------------------------------------
  // USER
  // ------------------------------------------------------------
  implicit val userFormat: OFormat[User] = Json.format[User]

  // ------------------------------------------------------------
  // EMPLOYEE
  // ------------------------------------------------------------
  implicit val employeeFormat: OFormat[Employee] = Json.format[Employee]

  // ------------------------------------------------------------
  // EQUIPMENT
  // ------------------------------------------------------------
  implicit val equipmentFormat: OFormat[Equipment] = Json.format[Equipment]

  // ------------------------------------------------------------
  // EQUIPMENT ALLOCATION
  // ------------------------------------------------------------
  implicit val equipmentAllocationFormat: OFormat[EquipmentAllocation] =
    Json.format[EquipmentAllocation]

}
