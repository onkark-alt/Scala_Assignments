ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "visitor-akka-service"
  )

libraryDependencies ++= Seq(
  // Akka Classic
  "com.typesafe.akka" %% "akka-actor"           % "2.8.5",
  "com.typesafe.akka" %% "akka-stream"          % "2.8.5",
  "com.typesafe.akka" %% "akka-slf4j"           % "2.8.5",

  // HTTP (classic works with this)
  "com.typesafe.akka" %% "akka-http"            % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",

  // Kafka (streams-based, works with classic)
  "com.typesafe.akka" %% "akka-stream-kafka"    % "3.0.0",

  // Kafka client
  "org.apache.kafka"  %  "kafka-clients"        % "3.5.1",

  // Email support
  "com.sun.mail"      %  "jakarta.mail"         % "2.0.1"
)
