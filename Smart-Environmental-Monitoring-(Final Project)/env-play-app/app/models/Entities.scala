package models

import java.sql.{Date, Timestamp}

case class Zone(
                 zoneId: Int,
                 name: String,
                 city: String,
                 latitude: Option[BigDecimal],
                 longitude: Option[BigDecimal],
                 createdAt: Option[Timestamp]
               )

case class Sensor(
                   sensorId: String,
                   zoneId: Int,
                   sensorType: String,
                   installedAt: Option[Timestamp],
                   status: Option[String]
                 )

case class PollutionThreshold(
                               thresholdId: Int,
                               zoneId: Int,
                               pm25Limit: Float,
                               pm10Limit: Float,
                               co2Limit: Float,
                               alertLevel: String
                             )

case class DailyPollutionSummary(
                                  recordId: Int,
                                  zoneId: Int,
                                  avgPm25: Float,
                                  avgPm10: Float,
                                  avgCo2: Float,
                                  anomalyCount: Option[Int],
                                  recordDate: Date,
                                  generatedAt: Option[Timestamp]
                                )
