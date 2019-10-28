import sbt._

object Dependencies {
  val catsVersion = "2.0.0"
  val mtlVersion = "0.6.0"
  val taglessVersion = "0.9"
  val fs2Version = "2.0.1"
  val newtypeVersion = "0.4.3"
  val circeVersion = "0.11.1"
  val scalacheckVersion = "1.14.0"
  val scalatestVersion = "3.0.8"
  val pureconfigVersion = "0.11.1"
  val kindProjectorVersion = "0.10.3"

  val compilerPlugins = Seq(
    compilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion)
  )

  lazy val cats = "org.typelevel" %% "cats-core" % catsVersion
  lazy val effect = "org.typelevel" %% "cats-effect" % catsVersion
  lazy val mtl = "org.typelevel" %% "cats-mtl-core" % mtlVersion
  lazy val tagless = "org.typelevel" %% "cats-tagless-macros" % taglessVersion
  lazy val fs2 = "co.fs2" %% "fs2-core" % fs2Version

  lazy val newtype = "io.estatico" %% "newtype" % newtypeVersion

  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val pureconfig = "com.github.pureconfig" %% "pureconfig" % pureconfigVersion

  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion % "it,test"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion % "it,test"


  lazy val allDependencies = Seq(
    cats,
    effect,
    mtl,
    tagless,
    fs2,
    newtype,
    pureconfig,
    scalaTest,
    scalaCheck
  ) ++ circe ++ compilerPlugins
}
