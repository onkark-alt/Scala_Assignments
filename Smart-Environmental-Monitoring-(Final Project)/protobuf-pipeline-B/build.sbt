//ThisBuild / version := "0.1.0-SNAPSHOT"
//
//ThisBuild / scalaVersion := "2.12.13"
//
//lazy val root = (project in file("."))
//  .settings(
//    name := "scala-pb"
//  )
//
//libraryDependencies ++= Seq(
//  "org.apache.spark" %% "spark-core" % "3.5.1",
//  "org.apache.spark" %% "spark-sql" % "3.5.1",
//  "org.apache.spark" %%"spark-protobuf"% "3.5.1",
//  // Include SparkSQL Protobuf suppor
//// Spark Kafka Integration
//"org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1",
//  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
//  "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.6"
//
//)
//


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
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",

      // ScalaPB runtime (required at runtime)
      "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.6" % "protobuf",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "com.datastax.spark" %% "spark-cassandra-connector" % "3.0.1",


    ),
    libraryDependencies += "com.datastax.oss" % "java-driver-core" % "4.15.0",

    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-aws" % "3.3.4",
      "com.amazonaws" % "aws-java-sdk-bundle" % "1.12.262"
    ),

      // === ⭐ REQUIRED: Generate Scala code from .proto files ===
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )
