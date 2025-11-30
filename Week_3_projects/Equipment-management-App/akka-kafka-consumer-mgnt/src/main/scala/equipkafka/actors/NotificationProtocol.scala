package equipkafka.actors

object NotificationProtocol {
  case class Handle(json: String, topic: String)
}
