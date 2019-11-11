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
  "org.wartremover" %% "wartremover" % "2.4.3" cross CrossVersion.full,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

enablePlugins(AutomateHeaderPlugin)
Test / headerSources / excludeFilter ~= { _ || "ResultAssertions.scala" }
