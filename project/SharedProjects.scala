import sbt.Build
import sbt.Keys._


object SharedProjects extends Build {

  import BaseBuild._

  //Adds syntax like IVec to scala and scala js and other specific cgta enhancements to each
  lazy val (oscalaX, oscala, oscalaJvm, oscalaSjs, oscalaJvmTest, oscalaSjsTest) = crossProject("oscala")
    .settingsSjs(libraryDependencies ++= Libs.sjs.dom)
    .tupledWithTests

  //Serialization
  lazy val (serlandX, serland, serlandJvm, serlandSjs, serlandJvmTest, serlandSjsTest) = crossProject("serland")
    .dependsOn(oscalaX)
    .settingsAll(macroSettings: _*)
    .settingsJvm(libraryDependencies ++= Libs.mongo)
    .tupledWithTests

  //Enumeration
  lazy val (cenumX, cenum, cenumJvm, cenumSjs, cenumJvmTest, cenumSjsTest) = crossProject("cenum")
    .dependsOn(serlandX)
    .settingsAll(macroSettings: _*)
    .tupledWithTests


}