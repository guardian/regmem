name := """play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)


libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "com.google.guava" % "guava" % "18.0",

  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7" % "provided",

  "org.parboiled" %% "parboiled" % "2.0.1",

  "org.jsoup" % "jsoup" % "1.8.1",

  "org.webjars" % "bootstrap" % "3.3.2-1",

  "org.elasticsearch" % "elasticsearch" % "1.4.4",

  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)


// deployment stuff
import NativePackagerKeys._
import com.typesafe.sbt.packager.MappingsHelper

// Don't include documentation in artifact
doc in Compile <<= target.map(_ / "none")

maintainer in Docker := "Graham Tackley <graham.tackley@theguardian.com>"

dockerExposedPorts in Docker := List(9000)

dockerBaseImage in Docker := "dockerfile/java:oracle-java8"

dockerPackageMappings in Docker := MappingsHelper.directory("data")
