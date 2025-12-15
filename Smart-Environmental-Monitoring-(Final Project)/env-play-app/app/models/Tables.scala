package models

import slick.jdbc.MySQLProfile.api._

class ZoneTable(tag: Tag) extends Table[Zone](tag, "zone") {
  def zoneId = column[Int]("zone_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def city = column[String]("city")
  def latitude = column[Option[BigDecimal]]("latitude")
  def longitude = column[Option[BigDecimal]]("longitude")
  def createdAt = column[Option[java.sql.Timestamp]]("created_at")

  def * = (zoneId, name, city, latitude, longitude, createdAt) <> (Zone.tupled, Zone.unapply)
}

class SensorTable(tag: Tag) extends Table[Sensor](tag, "sensor") {
  def sensorId = column[String]("sensor_id", O.PrimaryKey)
  def zoneId = column[Int]("zone_id")
  def sensorType = column[String]("sensor_type")
  def installedAt = column[Option[java.sql.Timestamp]]("installed_at")
  def status = column[Option[String]]("status")

  def * = (sensorId, zoneId, sensorType, installedAt, status) <> (Sensor.tupled, Sensor.unapply)
}

class PollutionThresholdTable(tag: Tag)
  extends Table[PollutionThreshold](tag, "pollution_threshold") {

  def thresholdId = column[Int]("threshold_id", O.PrimaryKey, O.AutoInc)
  def zoneId = column[Int]("zone_id")
  def pm25Limit = column[Float]("pm2_5_limit")
  def pm10Limit = column[Float]("pm10_limit")
  def co2Limit = column[Float]("co2_limit")
  def alertLevel = column[String]("alert_level")

  def * =
    (thresholdId, zoneId, pm25Limit, pm10Limit, co2Limit, alertLevel) <>
      (PollutionThreshold.tupled, PollutionThreshold.unapply)
}

class DailyPollutionSummaryTable(tag: Tag)
  extends Table[DailyPollutionSummary](tag, "daily_pollution_summary") {

  def recordId = column[Int]("record_id", O.PrimaryKey, O.AutoInc)
  def zoneId = column[Int]("zone_id")
  def avgPm25 = column[Float]("avg_pm2_5")
  def avgPm10 = column[Float]("avg_pm10")
  def avgCo2 = column[Float]("avg_co2")
  def anomalyCount = column[Option[Int]]("anomaly_count")
  def recordDate = column[java.sql.Date]("record_date")
  def generatedAt = column[Option[java.sql.Timestamp]]("generated_at")

  def * =
    (recordId, zoneId, avgPm25, avgPm10, avgCo2, anomalyCount, recordDate, generatedAt) <>
      (DailyPollutionSummary.tupled, DailyPollutionSummary.unapply)
}
