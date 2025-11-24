package services


import models.Equipment
import repositories.EquipmentRepository
import scala.concurrent.{ExecutionContext, Future}


class EquipmentService @javax.inject.Inject() (
                                                equipmentRepo: EquipmentRepository
                                              )(implicit ec: ExecutionContext) {


  def addEquipment(eq: Equipment): Future[Long] = equipmentRepo.insert(eq)


  def listEquipment(): Future[Seq[Equipment]] = equipmentRepo.findAll()


  def getEquipment(id: Long): Future[Option[Equipment]] = equipmentRepo.findById(id)


  def updateEquipmentStatus(id: Long, status: String, notes: Option[String]): Future[Int] =
    equipmentRepo.updateEquipmentStatus(id, status, notes)
}