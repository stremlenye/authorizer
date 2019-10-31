import Dependencies.allDependencies
import BuildOptions._
import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / useSuperShell := false
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "authorizer"
ThisBuild / organizationName := "com.authorizer"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "authorizer",
    scalacOptions ++= compilerOptions,
    resolvers ++= Seq(Resolver.sonatypeRepo("releases")),
    libraryDependencies ++= allDependencies,
    mainClass := Some("com.authorizer.Main"),
    Defaults.itSettings,
    fork in Test := true,
    fork in IntegrationTest := true,
    assemblyJarName in assembly := "authorizer.jar",
    test in assembly := {},
    assemblyOption in assembly := (assemblyOption in assembly).value
      .copy(prependShellScript = Some(defaultShellScript))
  ).configs(IntegrationTest)
