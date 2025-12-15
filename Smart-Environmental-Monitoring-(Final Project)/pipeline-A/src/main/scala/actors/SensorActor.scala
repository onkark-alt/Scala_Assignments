package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import models.SensorReading
import producer.KafkaJsonProducer
import config.AppConfig
import scala.util.Random
import io.circe.generic.auto._
import io.circe.syntax._

object SensorActor {

  case object GenerateReading

  def apply(sensorId: String, zoneId: Int): Behavior[GenerateReading.type] = {
    Behaviors.receive { (ctx, _) =>

      val pm25 = 10 + Random.nextDouble() * 40
      val pm10 = 20 + Random.nextDouble() * 80
      val co2  = 350 + Random.nextDouble() * 250

      // FIXED: map values to correct field names
      val reading = SensorReading(
        sensor_id = sensorId,
        zone_id = zoneId,
        pm2_5 = pm25,
        pm10 = pm10,
        co2_ppm = co2,            // FIXED FIELD NAME
        device_status = "OK",
        timestamp = System.currentTimeMillis()
      )

      KafkaJsonProducer.send(AppConfig.topic, sensorId, reading)
      Behaviors.same
    }
  }
}
