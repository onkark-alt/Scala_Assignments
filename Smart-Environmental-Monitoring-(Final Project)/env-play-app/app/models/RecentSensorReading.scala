package models

case class RecentSensorReading(
                                sensorId: String,
                                pm25: Float,
                                pm10: Float,
                                co2: Float,
                                timestamp: Long,
                                deviceStatus: String
                              )
