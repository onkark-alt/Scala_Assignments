package models

import play.api.libs.json._
import java.sql.{Timestamp, Date}

object JsonFormats {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsString(ts.toInstant.toString)
    def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsString(s) =>
        try JsSuccess(Timestamp.from(java.time.Instant.parse(s)))
        catch { case _: Exception => JsError("Invalid timestamp format") }
      case _ => JsError("String value expected for Timestamp")
    }
  }

  implicit val dateFormat: Format[Date] = new Format[Date] {
    def writes(d: Date): JsValue = JsString(d.toString)
    def reads(json: JsValue): JsResult[Date] = json match {
      case JsString(s) =>
        try JsSuccess(Date.valueOf(s))
        catch { case _: Exception => JsError("Invalid date format") }
      case _ => JsError("String value expected for Date")
    }
  }

  implicit val zoneFormat: OFormat[Zone] = Json.format[Zone]
  implicit val sensorFormat: OFormat[Sensor] = Json.format[Sensor]
  implicit val dailyPollutionSummaryFormat: OFormat[DailyPollutionSummary] = Json.format[DailyPollutionSummary]
  implicit val recentSensorReadingFormat: OFormat[RecentSensorReading] = Json.format[RecentSensorReading]

  // Optional: TodaySummary format
  implicit val todaySummaryFormat: OFormat[TodaySummary] = Json.format[TodaySummary]
}
