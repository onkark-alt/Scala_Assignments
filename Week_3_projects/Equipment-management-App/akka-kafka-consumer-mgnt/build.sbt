ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  // Akka Classic core
  "com.typesafe.akka" %% "akka-actor"          % "2.8.5",
  "com.typesafe.akka" %% "akka-slf4j"          % "2.8.5",

  // Akka Streams (works with Classic & Typed)
  "com.typesafe.akka" %% "akka-stream"         % "2.8.5",

  // Kafka connector  — works with Classic also
  "com.typesafe.akka" %% "akka-stream-kafka"   % "4.0.2",

  // JSON parsing (Play JSON)
  "com.typesafe.play" %% "play-json"           % "2.9.4",

  // Logging
  "ch.qos.logback"     % "logback-classic"     % "1.4.11",

  // Email
  "com.sun.mail"        % "jakarta.mail"        % "2.0.1"
)
