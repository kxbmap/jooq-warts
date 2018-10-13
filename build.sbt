name := "jooq-warts"
description := "Port jOOQ-checker to WartRemover"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint"
)
Test / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, x)) if x >= 12 => Seq("-Xlint:-unused")
    case _ => Seq.empty
  }
}

libraryDependencies ++= Seq(
  "org.jooq" % "jooq" % "3.11.5",
  "org.wartremover" %% "wartremover" % "2.3.7",
  "org.scalatest" %% "scalatest" % scalatestVersion(scalaVersion.value) % Test
)

def scalatestVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 13)) => "3.0.6-SNAP3"
    case _ => "3.0.5"
  }

enablePlugins(AutomateHeaderPlugin)
Test / headerSources / excludeFilter ~= { _ || "ResultAssertions.scala" }
