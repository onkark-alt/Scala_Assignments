package services


import java.time.LocalDateTime
import models.EquipmentAllocation
import repositories.{EquipmentAllocationRepository, EquipmentRepository}
import scala.concurrent.{ExecutionContext, Future}


class EquipmentAllocationService @javax.inject.Inject() (
                                                          allocationRepo: EquipmentAllocationRepository,
                                                          equipmentRepo: EquipmentRepository
                                                        )(implicit ec: ExecutionContext) {


  def allocate(a: EquipmentAllocation): Future[Long] = allocationRepo.insert(a)


  def getAllocation(id: Long): Future[Option[EquipmentAllocation]] = allocationRepo.findById(id)


  def markReturned(id: Long, cond: String): Future[Int] = {
    val now = LocalDateTime.now()
    allocationRepo.markReturned(id,now, cond)
  }


  def findOverdue(): Future[Seq[EquipmentAllocation]] = allocationRepo.findOverdue()
}