import sbt._
import sbt.Keys._
import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._
import Utilities._
import com.typesafe.sbt.pgp.PgpKeys._

object Publish {

  lazy val includeScaladoc = true

  lazy val publishSignedAction = { st: State =>
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(publishSigned in Global in ref, st)
  }


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
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts.copy(action = publishSignedAction),
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
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


  lazy val settingsJvm = ReleasePlugin.releaseSettings ++ settings
  lazy val settingsSjs = ReleasePlugin.releaseSettings ++ settings
}