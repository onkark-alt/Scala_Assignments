package Assignment2

import org.apache.spark.sql.{SaveMode, SparkSession}
import scala.util.Random

object UserPostPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("User Post Join Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. DATA GENERATION (1M USERS)
    // ------------------------------------------------------
    val userCount = 1000000

    val userRDD = spark.sparkContext
      .parallelize(1 to userCount, 30)
      .map { id =>
        val name = Random.alphanumeric.take(8).mkString
        val age = 15 + Random.nextInt(60)
        (id, name, age)
      }

    val userDF = userRDD.toDF("userId", "name", "age")

    // ------------------------------------------------------
    // 2. DATA GENERATION (2M POSTS)
    // ------------------------------------------------------
    val postCount = 2000000

    val postRDD = spark.sparkContext
      .parallelize(1 to postCount, 40)
      .map { pid =>
        val user = Random.nextInt(userCount) + 1
        val txt = Random.alphanumeric.take(20).mkString
        (pid, user, txt)
      }

    val postDF = postRDD.toDF("postId", "userId", "text")

    // ------------------------------------------------------
    // 3. JOIN USERS + POSTS ON userId
    // ------------------------------------------------------
    val joinedDF = userDF.join(postDF, "userId")

    println("Sample joined rows:")
    joinedDF.show(5, truncate = false)

    // ------------------------------------------------------
    // 4. COUNT POSTS PER AGE GROUP
    // ------------------------------------------------------
    val ageGroupPostsDF = joinedDF
      .groupBy("age")
      .count()
      .withColumnRenamed("count", "postCount")

    println("Posts per age group:")
    ageGroupPostsDF.show(10, truncate = false)

    // ------------------------------------------------------
    // 5. WRITE RESULTS TO JSON
    // ------------------------------------------------------
    ageGroupPostsDF.write
      .mode(SaveMode.Overwrite)
      .json("output/posts_per_age_json")
      Thread.sleep(30000)
    spark.stop()
  }
}
