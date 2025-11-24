package controllers

import javax.inject._
import play.api.mvc._
import services.KafkaProducerService
import scala.concurrent.ExecutionContext

@Singleton
class TestController @Inject()(
                                cc: ControllerComponents,
                                kafka: KafkaProducerService
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def testKafkaSend = Action.async {
    kafka.send("equipment.allocated", """{ "test": "allocation message" }""")
      .map { _ =>
        Ok("Kafka message sent!")
      }
  }
}
