import sbt._
import sbt.Keys._


object OpenProjects extends Build {

  import SharedProjects._

  lazy val root = Project("root", file("."))
    .aggregate(oscalaJvm, oscalaSjs, serlandJvm, serlandSjs, cenumJvm, cenumSjs)
//    .settings(crossScalaVersions := Seq("2.10.2", "2.11.1"))
    .settings(crossScalaVersions := Seq("2.10.2"))
    .settings(sbtrelease.ReleasePlugin.releaseSettings: _*)
    .settings(Publish.settings: _*)
    .settings(publish :=())
    .settings(publishLocal :=())

}