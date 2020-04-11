resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

name := "etf_job"

version := "0.0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-catalyst" % "2.4.4" % "provided"

libraryDependencies += "mrpowers" % "spark-daria" % "0.35.2-s_2.11"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.5"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"

libraryDependencies += "MrPowers" % "spark-fast-tests" % "0.20.0-s_2.11" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.apache.parquet" % "parquet-hadoop" % "1.11.0"
libraryDependencies += "org.apache.parquet" % "parquet-hadoop" % "1.11.0"
libraryDependencies += "org.apache.parquet" % "parquet-common" % "1.11.0"
libraryDependencies += "org.apache.parquet" % "parquet-column" % "1.11.0"
libraryDependencies += "org.apache.parquet" % "parquet-format" % "2.4.0"
libraryDependencies += "org.apache.parquet" % "parquet-encoding" % "1.11.0"
libraryDependencies += "org.apache.parquet" % "parquet-avro" % "1.11.0"

libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector_2.11" % "2.4.1"

// test suite settings
fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
// Show runtime of tests
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// JAR file settings

// don't include Scala in the JAR file
//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

// Add the JAR file naming conventions described here: https://github.com/MrPowers/spark-style-guide#jar-files
// You can add the JAR file naming conventions by running the shell script
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.last
}
//val meta = """META.INF(.)*""".r
//
//assemblyMergeStrategy in assembly := {
//  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
//  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
//  case n if n.contains("services") => MergeStrategy.concat
//  case n if n.startsWith("reference.conf") => MergeStrategy.concat
//  case n if n.endsWith(".conf") => MergeStrategy.concat
//  case meta(_) => MergeStrategy.discard
//  case x => MergeStrategy.first
//}
