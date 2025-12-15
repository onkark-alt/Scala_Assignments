package services

import javax.inject._
import slick.jdbc.MySQLProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import models._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ZoneService @Inject() (
                              protected val dbConfigProvider: DatabaseConfigProvider
                            )(implicit ec: ExecutionContext) {

  // ✅ Extract Database safely (Guice-friendly)
  private val db = dbConfigProvider.get.db

  private val zones   = TableQuery[ZoneTable]
  private val sensors = TableQuery[SensorTable]

  def zoneWithSensors(zoneId: Int): Future[(Zone, Seq[Sensor])] = {
    val zoneQ   = zones.filter(_.zoneId === zoneId).result.head
    val sensorQ = sensors.filter(_.zoneId === zoneId).result

    for {
      z <- db.run(zoneQ)
      s <- db.run(sensorQ)
    } yield (z, s)
  }

  def pollutionHistory(zoneId: Int): Future[Seq[DailyPollutionSummary]] = {
    val q = TableQuery[DailyPollutionSummaryTable]
      .filter(_.zoneId === zoneId)
      .sortBy(_.recordDate.desc)

    db.run(q.result)
  }
}
