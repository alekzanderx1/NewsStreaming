name := "NewsTrendStreaming"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.0.1",
  "org.apache.spark" %% "spark-streaming" % "3.0.1" % "provided"
)
