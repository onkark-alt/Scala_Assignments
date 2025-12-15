package services

import javax.inject._
import com.datastax.oss.driver.api.core._
import com.datastax.oss.driver.api.core.config._
import java.net.InetSocketAddress
import scala.jdk.CollectionConverters._
import models.RecentSensorReading

@Singleton
class CassandraService @Inject() () {

  private val region = "us-east-1"
  private val host   = "cassandra.us-east-1.amazonaws.com"
  private val port   = 9142

  // MUST be real AWS IAM credentials
  private val cassUser = "USERNAME"
  private val cassPass = "PASSWORD"

  private val truststorePath = "/Users/racit/cassandra_truststore.jks"
  private val truststorePassword = "changeit"

  lazy val session: CqlSession = {

    val loader = DriverConfigLoader.programmaticBuilder()
      .withString(
        DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER,
        region
      )
      .withString(
        DefaultDriverOption.SSL_ENGINE_FACTORY_CLASS,
        "com.datastax.oss.driver.internal.core.ssl.DefaultSslEngineFactory"
      )
      .withString(
        DefaultDriverOption.SSL_TRUSTSTORE_PATH,
        truststorePath
      )
      .withString(
        DefaultDriverOption.SSL_TRUSTSTORE_PASSWORD,
        truststorePassword
      )
      // 🔑 THIS FIXES YOUR ERROR
      .withBoolean(
        DefaultDriverOption.SSL_HOSTNAME_VALIDATION,
        false
      )
      .build()

    CqlSession.builder()
      .withConfigLoader(loader)
      .addContactPoint(new InetSocketAddress(host, port))
      .withLocalDatacenter(region)
      .withAuthCredentials(cassUser, cassPass)
      .build()
  }

  def recentReadings(zoneId: Int, limit: Int): Seq[RecentSensorReading] = {

    val rs = session.execute(
      s"""
         SELECT sensor_id, pm2_5, pm10, co2_ppm, device_status, timestamp
         FROM environment_grid.sensor_readings_by_zone
         WHERE zone_id = $zoneId
         LIMIT $limit
       """
    )

    rs.all().asScala.map { row =>
      RecentSensorReading(
        sensorId = row.getString("sensor_id"),
        pm25 = row.getFloat("pm2_5"),
        pm10 = row.getFloat("pm10"),
        co2 = row.getFloat("co2_ppm"),
        timestamp = row.getLong("timestamp"),
        deviceStatus = row.getString("device_status")
      )
    }.toSeq
  }
}
