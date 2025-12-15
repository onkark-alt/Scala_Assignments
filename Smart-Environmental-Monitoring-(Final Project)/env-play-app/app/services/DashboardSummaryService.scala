package services

import javax.inject._
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.services.s3.model.GetObjectRequest

import java.time.LocalDate
import example.daily.daily_avg_pm.DailyAvgPM
import example.daily.daily_avg_co2.DailyAvgCO2
import example.daily.daily_anomaly_count.DailyAnomalyCount
import models.TodaySummary

@Singleton
class DashboardSummaryService @Inject() () {

  private val bucket = "smart-env-monitering-bucket"

  // AWS credentials and region from env
  private val accessKey = sys.env.getOrElse("AWS_ACCESS_KEY", "")
  private val secretKey = sys.env.getOrElse("AWS_SECRET_KEY", "")
  private val region = sys.env.getOrElse("AWS_REGION", "us-east-1")

  // Lazy S3 client initialization
  lazy val s3: S3Client = S3Client.builder()
    .region(Region.of(region))
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
      )
    )
    .build()

  private def readBytes(key: String): Array[Byte] = {
    val obj = s3.getObject(
      GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build()
    )
    obj.readAllBytes()
  }

  def fetchTodaySummary(zoneId: Int): TodaySummary = {
    val today = LocalDate.now().minusDays(1).toString
    println(today)
    val pm = DailyAvgPM.parseFrom(readBytes(s"dashboard/daily_avg_pm/$today/zone_$zoneId.pb"))
    val co2 = DailyAvgCO2.parseFrom(readBytes(s"dashboard/daily_avg_co2/$today/zone_$zoneId.pb"))
    val anom = DailyAnomalyCount.parseFrom(readBytes(s"dashboard/daily_anomaly_counts/$today/zone_$zoneId.pb"))

    TodaySummary(
      zoneId = zoneId,
      zoneName = pm.zoneName,
      city = pm.city,
      avgPm25 = pm.avgPm25,
      avgPm10 = pm.avgPm10,
      avgCo2 = co2.avgCo2,
      anomalyCount = anom.anomalyCount,
      date = today
    )
  }
}
