import org.apache.spark.sql.SparkSession

case class Trip(
                 tripId: Long,
                 driverId: Int,
                 vehicleType: String,
                 startTime: String,
                 endTime: String,
                 startLocation: String,
                 endLocation: String,
                 distanceKm: Double,
                 fareAmount: Double,
                 paymentMethod: String,
                 customerRating: Double
               )

object RDDToDF {

  def safeToLong(s: String): Long = try s.toLong catch { case _: Exception => 0L }
  def safeToInt(s: String): Int = try s.toInt catch { case _: Exception => 0 }
  def safeToDouble(s: String): Double = try s.toDouble catch { case _: Exception => 0.0 }

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("RDD to DF Example")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val sc = spark.sparkContext

    val rawRdd = sc.textFile("/Users/racit/Documents/SPARK/spark-examples-main/urbanmove_trips.csv")

    val header = rawRdd.first()
    val rows = rawRdd.filter(_ != header)

    // ✅ FIXED: correct lambda syntax
    val splitted = rows.map(_.split(",", -1).map(_.trim))

    // Convert RDD → RDD[Trip]
    val tripRdd = splitted.map { cols =>
      Trip(
        safeToLong(cols(0)),
        safeToInt(cols(1)),
        cols(2),
        cols(3),
        cols(4),
        cols(5),
        cols(6),
        safeToDouble(cols(7)),
        safeToDouble(cols(8)),
        cols(9),
        safeToDouble(cols(10))
      )
    }

    // Convert to DataFrame
    val tripDF = spark.createDataFrame(tripRdd)

    tripDF.show(5, truncate = false)
    tripDF.printSchema()
  }
}
