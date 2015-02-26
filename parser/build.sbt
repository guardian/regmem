scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "com.google.guava" % "guava" % "18.0",

  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7" % "provided",

  "org.parboiled" %% "parboiled" % "2.0.1",

  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
