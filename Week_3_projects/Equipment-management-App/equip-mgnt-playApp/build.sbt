name := "equip-mgnt-playApp"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(

    libraryDependencies ++= Seq(
      guice,
      filters,

      // JSON
      "com.typesafe.play" %% "play-json" % "2.9.4",

      // Play 3 Asset Handling (REQUIRED)


      // Slick + Play Slick
      "org.playframework" %% "play-slick" % "6.1.0",
      "org.playframework" %% "play-slick-evolutions" % "6.1.0",

      // MySQL
      "mysql" % "mysql-connector-java" % "8.0.33",

      // Kafka


      // Akka
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
      "com.typesafe.akka" %% "akka-stream"      % "2.8.5",
      "com.typesafe.akka" %% "akka-slf4j"       % "2.8.5",
      "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",

      // JWT Authentication (optional)
      "com.auth0" % "java-jwt" % "4.3.0",
      "org.mindrot" % "jbcrypt" % "0.4",

      "org.apache.kafka" %% "kafka" % "3.7.0",
      "org.apache.kafka" % "kafka-clients" % "3.7.0",


// Testing
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,

    ),

    // Needed for Play 3 (Java 17 recommended)
    javacOptions ++= Seq("--release", "17")
  )
