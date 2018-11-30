name := "Scala Playground"
version := "1.0"
scalaVersion := "2.11.12"

libraryDependencies += "at.ac.ait" %% "graphsense-clustering" % "0.3.3"

val sparkVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion  // % "compile" withSources(),
  "org.apache.spark" %% "spark-streaming" % sparkVersion
)