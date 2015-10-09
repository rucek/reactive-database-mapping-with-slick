name := "reactive-database-mapping-with-slick"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.7.21",
  "com.h2database" % "h2" % "1.4.191"
)