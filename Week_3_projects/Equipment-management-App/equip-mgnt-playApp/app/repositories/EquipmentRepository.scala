package repositories

import models.{Equipment, EquipmentTable}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

@Singleton
class EquipmentRepository @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider
                                   )(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val equipment = EquipmentTable.table

  def insert(eq: Equipment): Future[Long] =
    db.run((equipment returning equipment.map(_.id)) += eq)

  def findAll(): Future[Seq[Equipment]] =
    db.run(equipment.result)

  def findById(id: Long): Future[Option[Equipment]] =
    db.run(equipment.filter(_.id === id).result.headOption)

  def updateEquipmentStatus(
                             id: Long,
                             newStatus: String,
                             notes: Option[String]
                           ): Future[Int] =
    db.run(
      equipment
        .filter(_.id === id)
        .map(e => (e.status, e.conditionNotes))
        .update((newStatus, notes))
    )
}
