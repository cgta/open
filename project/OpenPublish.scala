import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys
import sbtrelease.{ReleasePlugin, Version}

object Publish {
  lazy val settings = Seq[Setting[_]](
    organization := "biz.cgta",
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    (publishArtifact in Test) := false,
    pomIncludeRepository := { _ => false},
    pomExtra :=
      <url>https://github.com/cgta/otest</url>
        <licenses>
          <license>
            <name>MIT license</name>
            <url>http://www.opensource.org/licenses/mit-license.php</url>
          </license>
        </licenses>
        <scm>
          <url>git://github.com/cgta/otest.git</url>
          <connection>scm:git://github.com/cgta/otest.git</connection>
        </scm>
        <developers>
          <developer>
            <id>benjaminjackman</id>
            <name>Benjamin Jackman</name>
            <url>https://github.com/benjaminjackman</url>
          </developer>
        </developers>

  )

  lazy val settingsJvm = settings ++ ReleasePlugin.releaseSettings
  lazy val settingsSjs = settings ++ ReleasePlugin.releaseSettings
}