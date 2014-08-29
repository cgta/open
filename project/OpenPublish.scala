import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys
import sbtrelease.Version

object Publish {
  lazy val settings = Seq[Setting[_]](
    // organization := "biz.cgta.orange",
    // publishArtifact in Test := false,
    // credentials += Credentials("Sonatype Nexus Repository Manager", "cgta-vm-125", "deployment", "deployment123"),
    // publishTo := {
    //   val nexus = "http://cgta-vm-125:26881/nexus/content/repositories/"
    //   if (version.value.trim.endsWith("SNAPSHOT")) {
    //     Some("cgta-snapshots" at nexus + "snapshots")
    //   } else {
    //     Some("cgta-releases" at nexus + "releases")
    //   }
    // },
    // ReleaseKeys.versionBump := {
    //   val releaseType = System.getenv("RELEASE_TYPE")
    //   if (releaseType == "Major") {
    //     Version.Bump.Major
    //   } else if (releaseType == "Minor") {
    //     Version.Bump.Minor
    //   } else {
    //     Version.Bump.Bugfix
    //   }
    // }
    )
}