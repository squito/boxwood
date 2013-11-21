import sbt._
import Keys._

object SparkBuild extends Build {
  lazy val core = Project("core", file("core"), settings = coreSettings)

  val qf = "http://repo.quantifind.com/content/repositories/"
  def sharedSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.10.2",
    organization := "com.quantifind",
    scalacOptions := Seq(/*"-deprecation",*/ "-unchecked", "-optimize"), // -deprecation is too noisy due to usage of old Hadoop API, enable it once that's no longer an issue
    unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath },
    retrieveManaged := true,
    transitiveClassifiers in Scope.GlobalScope := Seq("sources"),
    //publishTo <<= baseDirectory { base => Some(Resolver.file("Local", base / "target" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern))) },
    publishTo <<= version {
      (v: String) =>
        Some("snapshots" at qf + "ext-snapshots")
    },
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.0" % "test",
      "com.chuusai" % "shapeless_2.10.2" % "2.0.0-M1" % "test"
    )
  )

  val slf4jVersion = "1.6.1"

  def coreSettings = sharedSettings ++ Seq(
    name := "Boxwood",
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.16",
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.slf4j" % "slf4j-log4j12" % slf4jVersion
    )
  )

}
