
import sbt._

object Dependencies {

  private val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % Versions.scalaTest,
    "org.scalactic" %% "scalactic" % Versions.scalaTest
  ).map(_ % Test)

  private val akkaActors: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % Versions.akka.actors
  )

  private val akkaHttp: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http-core" % Versions.akka.http,
    "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akka.http
  )

  private val akkaLogging: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % Versions.akka.actors,
    "ch.qos.logback"    %  "logback-classic" % Versions.logback,
    "org.apache.logging.log4j" % "log4j-core" % Versions.log4j2,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j2
  )

  private val akkaTest: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-testkit" % Versions.akka.actors
  ).map(_ % Test)

  val coreMainDeps: Seq[ModuleID] = akkaActors.union(akkaHttp).union(akkaLogging)
  val coreTestDeps: Seq[ModuleID] = scalaTest.union(akkaTest)

}
