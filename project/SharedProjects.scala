import sbt.Build
import sbt.Keys._


object SharedProjects extends Build {

  import BaseBuild._

  //Adds syntax like IVec to scala and scala js and other specific cgta enhancements to each
  lazy val (oscalaX, oscala, oscalaJvm, oscalaSjs, oscalaJvmTest, oscalaSjsTest) = crossProject("oscala")
    .settingsSjs(libraryDependencies ++= Libs.sjs.dom)
    .tupledWithTests

}