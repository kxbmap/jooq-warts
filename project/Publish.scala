import com.jsuereth.sbtpgp.PgpKeys._
import com.jsuereth.sbtpgp.SbtPgp
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.GitHubHosting
import xerial.sbt.Sonatype.SonatypeKeys._

object Publish extends AutoPlugin {

  override def requires: Plugins = Sonatype && ReleasePlugin && SbtPgp

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = Seq(
    organization := "com.github.kxbmap",
    organizationName := "Tsukasa Kitachi",
    startYear := Some(2018),
    licenses := Seq(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
    ),
    publishMavenStyle := true,
    publishTo := sonatypePublishTo.value,
    sonatypeProjectHosting := Some(GitHubHosting("kxbmap", "configs", organizationName.value, "kxbmap@gmail.com")),
    pomIncludeRepository := { _ => false },

    releaseCrossBuild := true,
    releasePublishArtifactsAction := publishSigned.value,
    releaseProcess := Seq(
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion
    )
  )

}
