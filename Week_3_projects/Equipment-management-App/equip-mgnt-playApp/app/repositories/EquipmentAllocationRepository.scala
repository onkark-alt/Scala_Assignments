package repositories

import models.{EquipmentAllocation, EquipmentAllocationTable}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

@Singleton
class EquipmentAllocationRepository @Inject()(
                                               protected val dbConfigProvider: DatabaseConfigProvider
                                             )(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val allocation = EquipmentAllocationTable.table

  def insert(a: EquipmentAllocation): Future[Long] =
    db.run((allocation returning allocation.map(_.id)) += a)

  def findById(id: Long): Future[Option[EquipmentAllocation]] =
    db.run(allocation.filter(_.id === id).result.headOption)

  def findActiveByEquipmentId(equipmentId: Long): Future[Option[EquipmentAllocation]] =
    db.run(
      allocation
        .filter(a => a.equipmentId === equipmentId && a.actualReturnDate.isEmpty)
        .result
        .headOption
    )

  def markReturned(
                    id: Long,

                    actualReturnDate: LocalDateTime,
                    returnedCondition: String
                  ): Future[Int] =
    db.run(
      allocation
        .filter(_.id === id)
        .map(a => (a.actualReturnDate, a.returnedCondition))
        .update((Some(actualReturnDate), returnedCondition
        )))

  def findOverdue(): Future[Seq[EquipmentAllocation]] =
    db.run(
      allocation
        .filter(a =>
          a.actualReturnDate.isEmpty &&
            a.expectedReturnDate < LocalDateTime.now()
        )
        .result
    )
}
