ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.13"

lazy val root = (project in file("."))
  .settings(
    name := "scala-pb",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.1",
      "org.apache.spark" %% "spark-sql" % "3.5.1",
      "org.apache.spark" %% "spark-protobuf" % "3.5.1",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1",

      // ScalaPB Runtime for protobuf decoding inside Spark
      "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.6",
      // REQUIRED for generating .desc files
      "com.thesamet.scalapb" %% "compilerplugin" % "0.11.6",
      // Extra dependency for JSON printing support (optional)
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2"
    ),

    // === ⭐ AUTO generate .scala + .desc files from .proto ===
    PB.targets in Compile := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb",
      PB.protocDescriptor -> (Compile / resourceManaged).value / "protobuf_desc"
    )
  )
