package config

object AppConfig {
  val kafkaBootstrap = "localhost:9092"
  val topic = "air_sensor_readings"
  val sensorCount = 1000
  val intervalSeconds = 10
}