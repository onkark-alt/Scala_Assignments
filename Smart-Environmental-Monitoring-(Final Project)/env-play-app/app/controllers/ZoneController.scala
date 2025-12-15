package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services._
import models._
import models.JsonFormats._
import scala.concurrent.ExecutionContext

@Singleton
class ZoneController @Inject() (
                                 cc: ControllerComponents,
                                 zoneService: ZoneService,
                                 cassandraService: CassandraService,
                                 dashboardService: DashboardSummaryService
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def zone(id: Int) = Action.async {
    zoneService.zoneWithSensors(id).map {
      case (z, s) =>
        Ok(Json.obj(
          "zone" -> Json.toJson(z),
          "sensors" -> Json.toJson(s)
        ))
    }
  }

  def recentReadings(id: Int, limit: Int) = Action {
    Ok(Json.toJson(cassandraService.recentReadings(id, limit)))
  }

  def dailySummary(id: Int) = Action {
    Ok(Json.toJson(dashboardService.fetchTodaySummary(id)))
  }

  def pollutionHistory(id: Int) = Action.async {
    zoneService.pollutionHistory(id).map { history =>
      Ok(Json.toJson(history)) // works because dailyPollutionSummaryFormat is defined
    }
  }
}
