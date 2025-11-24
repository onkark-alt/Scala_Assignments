ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed"   % "2.8.5",
  "com.typesafe.akka" %% "akka-stream"        % "2.8.5",
  "com.typesafe.akka" %% "akka-slf4j"         % "2.8.5",
  "com.typesafe.akka" %% "akka-stream-kafka"  % "4.0.2",
  "com.typesafe.play" %% "play-json"          % "2.9.4",    // JSON parsing
  "ch.qos.logback"    %  "logback-classic"    % "1.4.11",

  "com.sun.mail" % "jakarta.mail" % "2.0.1"
)
