ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "environment-play-service",

    libraryDependencies ++= Seq(
      guice,

      // ===== Slick / MySQL (Play-managed versions) =====
      "com.typesafe.play" %% "play-slick" % "5.1.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
      "mysql" % "mysql-connector-java" % "8.0.33",

      // ===== Cassandra =====
      "com.datastax.oss" % "java-driver-core" % "4.17.0",

      // ===== AWS S3 =====
      "software.amazon.awssdk" % "s3" % "2.25.63",

      // ===== ScalaPB runtime ONLY (compiler plugin comes from sbt plugin) =====
      "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.15" % "protobuf"
    ),

    // ===== Protobuf code generation =====
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value
    )
  )

// ✅ Force the version Play 3 / Twirl 2 expects
dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0"
)

// ✅ Do not fail build on known-safe Play evictions
ThisBuild / evictionErrorLevel := Level.Warn
