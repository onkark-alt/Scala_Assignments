package models

import play.api.libs.json._

case class TodaySummary(
                         zoneId: Int,
                         zoneName: String,
                         city: String,
                         avgPm25: Float,
                         avgPm10: Float,
                         avgCo2: Float,
                         anomalyCount: Int,
                         date: String
                       )