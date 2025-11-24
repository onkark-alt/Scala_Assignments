package models

import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.time.LocalDateTime


case class EquipmentAllocation(
                                id: Option[Long],
                                employeeId: Long,
                                equipmentId: Long,
                                allocationDate: LocalDateTime,
                                expectedReturnDate: LocalDateTime,
                                actualReturnDate: Option[LocalDateTime],
                                returnedCondition: String
                              )

class EquipmentAllocationTable(tag: Tag) extends Table[EquipmentAllocation](tag, "equipment_allocation") {
  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def employeeId = column[Long]("employee_id")
  def equipmentId = column[Long]("equipment_id")
  def allocationDate = column[LocalDateTime]("allocation_date")
  def expectedReturnDate = column[LocalDateTime]("expected_return_date")
  def actualReturnDate = column[Option[LocalDateTime]]("actual_return_date")
  def returnedCondition = column[String]("returned_condition")

  // ❗ FIX: Explicit tuple mapping
  def * = (
    id.?,
    employeeId,
    equipmentId,
    allocationDate,
    expectedReturnDate,
    actualReturnDate,
    returnedCondition
  ) <> ((EquipmentAllocation.apply _).tupled, EquipmentAllocation.unapply)
}

object EquipmentAllocationTable {
  val table = TableQuery[EquipmentAllocationTable]
}
