name := "Scala Playground"
version := "1.0"
scalaVersion := "2.11.12"
libraryDependencies += "at.ac.ait" %% "linking" % "1.0"

initialCommands in console += "import linking.common._"