
val projectVersion = "0.0.1"
val projectName = "auction"
val scalaProjectVersion = "2.13.4"

lazy val commonSettings = Seq(
  version := projectVersion,
  scalaVersion := scalaProjectVersion,
  resolvers ++= Seq(
    Resolver.mavenCentral,
    Resolver.mavenLocal,
    Resolver.bintrayRepo("sbt-native-packager", "maven"),
    Resolver.bintrayRepo("sbt-assembly", "maven")
  ),
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  scalafmtOnCompile := true
)

lazy val core = (project in file("."))
  .settings(commonSettings)
  .enablePlugins(JavaServerAppPackaging, MultiJvmPlugin)
  .configs(MultiJvm)
  .settings(
    name := s"$projectName",
    libraryDependencies ++= Dependencies.coreMainDeps ++ Dependencies.coreTestDeps,
    mainClass in assembly := Some("com.romanidze.auction.app.SimpleMain"),
    assemblyJarName in assembly := s"auction-$projectVersion.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("org", "slf4j", xs@_*) => MergeStrategy.last
      case x => (assemblyMergeStrategy in assembly).value(x)
    }
  )

addCommandAlias(
  "buildFrontend",
  "; " +
    "set mainClass in assembly := Some(\"com.romanidze.auction.app.FrontendMain\");" +
    " set assemblyJarName in assembly := \"auction-frontend-0.0.1.jar\" ; assembly"
)
addCommandAlias(
  "buildBackend",
  ";" +
    " set mainClass in assembly := Some(\"com.romanidze.auction.app.BackendMain\");" +
    " set assemblyJarName in assembly := \"auction-backend-0.0.1.jar\" ;" +
    " assembly"
)
