package Assignment2

package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object StudentScorePipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Student Score Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -----------------------------------------
    // ✅ Data Generation for 1.5 Million Students
    // -----------------------------------------
    val numStudents = 1500000

    val studentRDD = spark.sparkContext.parallelize(1 to numStudents, 20)
      .map { id =>
        val name = Random.alphanumeric.take(6).mkString
        val score = Random.nextInt(100)
        (id, name, score)
      }

    val studentDF = studentRDD.toDF("studentId", "name", "score")

    // -----------------------------------------
    // 📘 Exercise: Sort by score (desc)
    // -----------------------------------------
    val sortedDF = studentDF.orderBy($"score".desc)

    // -----------------------------------------
    // 📘 Write output to JSON (slowest format)
    // -----------------------------------------
    sortedDF.write
      .mode(SaveMode.Overwrite)
      .json("output/students_sorted_json")

    println("✅ Sorting done (broad shuffle).")
    println("📁 JSON written to: output/students_sorted_json")
    Thread.sleep(300000)
    spark.stop()
  }
}

