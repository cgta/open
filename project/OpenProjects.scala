import sbt._
import sbt.Keys._


object OpenProjects extends Build {

  import SharedProjects._

  lazy val root = Project("root", file("."))
    .aggregate(oscalaJVM, oscalaSJS, serlandJVM, serlandSJS, cenumJVM, cenumSJS)
//    .settings(crossScalaVersions := Seq("2.10.2", "2.11.1"))
    .settings(crossScalaVersions := Seq("2.11.7"))
    .settings(sbtrelease.ReleasePlugin.releaseSettings: _*)
    .settings(Publish.settings: _*)
    .settings(publish :=())
    .settings(publishLocal :=())

}