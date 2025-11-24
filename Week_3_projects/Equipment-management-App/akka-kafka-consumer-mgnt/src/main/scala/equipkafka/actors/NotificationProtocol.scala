package equipkafka.actors

object NotificationProtocol {
  // simple protocol - pass raw JSON string and topic if needed
  final case class Handle(json: String, topic: String)
}
