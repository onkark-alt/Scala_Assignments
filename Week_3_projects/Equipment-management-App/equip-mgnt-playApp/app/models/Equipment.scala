package models

import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.time.LocalDateTime

case class Equipment(
                      id: Option[Long],
                      name: String,
                      `type`: String,
                      status: String,
                      conditionNotes: Option[String]
                    )

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "equipment") {
  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def equipmentType = column[String]("type")
  def status = column[String]("status")
  def conditionNotes = column[Option[String]]("condition_notes")

  def * = (id.?, name, equipmentType, status, conditionNotes) <> ((Equipment.apply _).tupled, Equipment.unapply)
}

object EquipmentTable {
  val table = TableQuery[EquipmentTable]
}
