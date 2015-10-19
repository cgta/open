import sbt._
import sbt.Keys._
import sbt.Build
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


object SharedProjects extends Build {

  import BaseBuild._
  import LibsHelp.Implicits._

  //Adds syntax like IVec to scala and scala js and other specific cgta enhancements to each
  lazy val oscala = crossProject.in(file("oscala")).configure(xp("oscala", _))
    .jsSettings(Libs.Dom.settings: _*)
    .jsConfigure(_.copy(id = "oscalaSJS"))
    .settings(sbtide.Keys.ideBasePackages :=  List("cgta.oscala"))
    .jvmSettings(Assembly.settings: _*)

  lazy val oscalaJVM = oscala.jvm
  lazy val oscalaSJS = oscala.js

  //Serialization
  lazy val serland = crossProject.in(file("serland")).configure(xp("serland", _))
     .dependsOn(oscala)
     .settings(macroSettings: _*)
     .settings(sbtide.Keys.ideBasePackages :=  List("cgta.serland"))
     .jsConfigure(_.copy(id = "serlandSJS"))
     .addLibs(
       Libs.mongo
     )
  lazy val serlandJVM = serland.jvm
  lazy val serlandSJS = serland.js

  //Enumeration
  lazy val cenum = crossProject.in(file("cenum")).configure(xp("cenum", _))
     .dependsOn(serland)
     .settings(sbtide.Keys.ideBasePackages :=  List("cgta.cenum"))
     .settings(macroSettings: _*)
     .jsConfigure(_.copy(id = "cenumSJS"))
  lazy val cenumJVM = cenum.jvm
  lazy val cenumSJS = cenum.js

}