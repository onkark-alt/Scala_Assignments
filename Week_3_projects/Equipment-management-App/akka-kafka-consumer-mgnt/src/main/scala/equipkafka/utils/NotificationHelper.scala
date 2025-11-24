package equipkafka.utils

object NotificationHelper {

  def notifyInventory(equipmentId: Option[Long], employeeId: Option[Long]): Unit = {
    // placeholder: call inventory microservice or DB
    println(s"[NotificationHelper] notifyInventory: equipment=$equipmentId to employee=$employeeId")
  }

  def handleReturn(allocationId: Option[Long], equipmentId: Option[Long], condition: Option[String]): Unit = {
    println(s"[NotificationHelper] handleReturn: allocation=$allocationId equipment=$equipmentId condition=$condition")
    // if condition indicates damage, escalate to maintenance; otherwise mark available
    val needsMaintenance = condition.exists(_.toLowerCase.contains("damage"))
    if (needsMaintenance) {
      println(s"[NotificationHelper] -> escalated to maintenance for equipment=$equipmentId")
    }
  }

  def alertMaintenance(equipmentId: Option[Long], notes: Option[String]): Unit = {
    println(s"[NotificationHelper] alertMaintenance: equipment=$equipmentId notes=$notes")
    // actual implementation -> API call or email / SMS
  }
}
