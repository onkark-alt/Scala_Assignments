package models

case class SensorReading(
                          sensor_id: String,
                          zone_id: Int,
                          pm2_5: Double,
                          pm10: Double,
                          co2_ppm: Double,
                          device_status: String,
                          timestamp: Long
                        )
