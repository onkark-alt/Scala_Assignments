package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object EmployeeSalaryPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Employee Salary Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -------------------------
    // ✅ Data Generation (FIXED)
    // -------------------------

    val numEmp = 1000000

    val departments: Array[String] = Array("HR", "IT", "Sales", "Finance")   // FIXED TYPE

    val empRDD = spark.sparkContext.parallelize(1 to numEmp, 20)
      .map { id =>
        val name = Random.alphanumeric.take(7).mkString
        val dept = departments(Random.nextInt(4))      // FIXED
        val salary = 30000 + Random.nextInt(70000)
        (id, name, dept, salary)
      }

    val empDF = empRDD.toDF("empId", "name", "department", "salary")

    // -------------------------
    // 📘 Exercise 1: Avg Salary
    // -------------------------

    val avgSalaryDF = empDF
      .groupBy("department")
      .avg("salary")
      .withColumnRenamed("avg(salary)", "avg_salary")

    // Write to CSV
    avgSalaryDF.write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv("output/employee_avg_salary")

    // -------------------------
    // 📘 Exercise 2: Load CSV back
    // -------------------------

    val loadedDF = spark.read
      .option("header", "true")
      .csv("output/employee_avg_salary")

    println(" Schema After Reading CSV (Everything Becomes String):")
    loadedDF.printSchema()
    Thread.sleep(300000)
    spark.stop()
  }
}
