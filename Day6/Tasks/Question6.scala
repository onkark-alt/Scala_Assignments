import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException}
import scala.io.StdIn

// Database connection helper
object DatabaseConnection {
  val url = "jdbc:mysql://azuremysqxyz.mysql.database.azure.com:3306/xxx"
  val username = "xxx"
  val password = "xxx" 

  def getConnection(): Connection = {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      DriverManager.getConnection(url, username, password)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("Database connection failed!")
    }
  }
}

// DAO for CRUD operations
object TrafficDAO {
  def addVehicle(license: String, vType: String, owner: String): Unit = {
    val conn = DatabaseConnection.getConnection()
    val query = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setString(1, license)
      stmt.setString(2, vType)
      stmt.setString(3, owner)
      stmt.executeUpdate()
      println("✅ Vehicle added successfully!")
    } finally {
      stmt.close()
      conn.close()
    }
  }

  def addSignal(location: String, status: String): Unit = {
    val conn = DatabaseConnection.getConnection()
    val query = "INSERT INTO TrafficSignals (location, status) VALUES (?, ?)"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setString(1, location)
      stmt.setString(2, status)
      stmt.executeUpdate()
      println("✅ Traffic signal added!")
    } finally {
      stmt.close()
      conn.close()
    }
  }

  def recordViolation(vehicleId: Int, signalId: Int, violationType: String): Unit = {
    val conn = DatabaseConnection.getConnection()
    val query = "INSERT INTO Violations (vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, NOW())"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setInt(1, vehicleId)
      stmt.setInt(2, signalId)
      stmt.setString(3, violationType)
      stmt.executeUpdate()
      println("Violation recorded!")
    } finally {
      stmt.close()
      conn.close()
    }
  }

  def updateSignalStatus(signalId: Int, newStatus: String): Unit = {
    val conn = DatabaseConnection.getConnection()
    val query = "UPDATE TrafficSignals SET status = ? WHERE signal_id = ?"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setString(1, newStatus)
      stmt.setInt(2, signalId)
      val rows = stmt.executeUpdate()
      if (rows > 0) println("🔄 Signal status updated!") else println("Signal not found!")
    } finally {
      stmt.close()
      conn.close()
    }
  }

  def viewVehicles(): Unit = {
    val conn = DatabaseConnection.getConnection()
    val stmt = conn.createStatement()
    val rs = stmt.executeQuery("SELECT * FROM Vehicles")
    println("\n--- Vehicles ---")
    while (rs.next()) {
      println(s"${rs.getInt("vehicle_id")} | ${rs.getString("license_plate")} | ${rs.getString("vehicle_type")} | ${rs.getString("owner_name")}")
    }
    rs.close()
    stmt.close()
    conn.close()
  }

  def viewSignals(): Unit = {
    val conn = DatabaseConnection.getConnection()
    val stmt = conn.createStatement()
    val rs = stmt.executeQuery("SELECT * FROM TrafficSignals")
    println("\n--- Traffic Signals ---")
    while (rs.next()) {
      println(s"${rs.getInt("signal_id")} | ${rs.getString("location")} | ${rs.getString("status")}")
    }
    rs.close()
    stmt.close()
    conn.close()
  }

  def viewViolations(): Unit = {
    val conn = DatabaseConnection.getConnection()
    val stmt = conn.createStatement()
    val rs = stmt.executeQuery("SELECT * FROM Violations")
    println("\n--- Violations ---")
    while (rs.next()) {
      println(s"${rs.getInt("violation_id")} | Vehicle ${rs.getInt("vehicle_id")} | Signal ${rs.getInt("signal_id")} | ${rs.getString("violation_type")} | ${rs.getTimestamp("timestamp")}")
    }
    rs.close()
    stmt.close()
    conn.close()
  }
}

// Menu-driven Main Program
object TrafficMain extends App {
  var running = true

  while (running) {
    println(
      """
        |========= Smart Traffic Management =========
        |1. Add Vehicle
        |2. Add Traffic Signal
        |3. Record Violation
        |4. Update Signal Status
        |5. View Vehicles
        |6. View Traffic Signals
        |7. View Violations
        |8. Exit
        |--------------------------------------------
        |Enter your choice:
        |""".stripMargin)

    val choice = StdIn.readInt()

    choice match {
      case 1 =>
        print("Enter License Plate: "); val lp = StdIn.readLine()
        print("Enter Vehicle Type: "); val vt = StdIn.readLine()
        print("Enter Owner Name: "); val ow = StdIn.readLine()
        TrafficDAO.addVehicle(lp, vt, ow)

      case 2 =>
        print("Enter Location: "); val loc = StdIn.readLine()
        print("Enter Status (green/yellow/red): "); val st = StdIn.readLine()
        TrafficDAO.addSignal(loc, st)

      case 3 =>
        print("Enter Vehicle ID: "); val vid = StdIn.readInt()
        print("Enter Signal ID: "); val sid = StdIn.readInt()
        print("Enter Violation Type: "); val vtype = StdIn.readLine()
        TrafficDAO.recordViolation(vid, sid, vtype)

      case 4 =>
        print("Enter Signal ID: "); val sid = StdIn.readInt()
        print("Enter New Status: "); val ns = StdIn.readLine()
        TrafficDAO.updateSignalStatus(sid, ns)

      case 5 => TrafficDAO.viewVehicles()
      case 6 => TrafficDAO.viewSignals()
      case 7 => TrafficDAO.viewViolations()
      case 8 =>
        println("Exiting system.")
        running = false

      case _ => println("Invalid choice! Try again.")
    }
  }
}
