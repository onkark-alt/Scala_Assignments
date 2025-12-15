name := "akka-sensor-simulation"
version := "1.0"
scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.4",
  "com.typesafe.akka" %% "akka-stream" % "2.8.4",
  "ch.qos.logback" % "logback-classic" % "1.4.11",
  "org.apache.kafka" % "kafka-clients" % "3.7.0",
  "io.circe" %% "circe-core" % "0.14.7",
  "io.circe" %% "circe-generic" % "0.14.7",
  "io.circe" %% "circe-parser" % "0.14.7"
)
