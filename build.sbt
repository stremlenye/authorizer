import Dependencies.allDependencies
import BuildOptions._

ThisBuild / useSuperShell    := false
ThisBuild / scalaVersion     := "2.12.9"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.coding-challenge"
ThisBuild / organizationName := "com/challenge"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "nubank",
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= allDependencies,
    Defaults.itSettings,
    fork in Test := true,
    fork in IntegrationTest := true
  ).configs(IntegrationTest)

