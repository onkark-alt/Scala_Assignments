package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import config.AppConfig
import scala.util.Random

object Supervisor {
  def apply(): Behavior[Any] = Behaviors.setup { ctx =>
    val sensors = (1 to AppConfig.sensorCount).map { i =>
      ctx.spawn(SensorActor(s"SEN-$i", Random.nextInt(10) + 1), s"sens-$i")
    }

    import scala.concurrent.duration._
    implicit val scheduler = ctx.system.scheduler
    implicit val ec = ctx.executionContext

    ctx.log.info(s"Starting ${sensors.size} virtual sensors...")

    ctx.system.scheduler.scheduleAtFixedRate(0.seconds, AppConfig.intervalSeconds.seconds) { () =>
      sensors.foreach(_ ! SensorActor.GenerateReading)
    }

    Behaviors.empty
  }
}
