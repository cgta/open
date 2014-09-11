import cgta.sbtxsjs.SbtXSjsPlugin.XSjsProjects
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import de.johoop.jacoco4sbt.JacocoPlugin
import org.sbtidea.SbtIdeaPlugin
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys
import sbtrelease.ReleasePlugin

import scala.scalajs.sbtplugin.ScalaJSPlugin
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys


object BaseBuild extends Build with UniversalKeys {
  sys.props("scalac.patmat.analysisBudget") = "512"
  //org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(ch.qos.logback.classic.Level.INFO)


  lazy val basicSettings = Seq[Setting[_]](scalaVersion := "2.10.2") ++ scalacSettings ++ promptSettings

  lazy val promptSettings = Seq[Setting[_]](
    shellPrompt <<= (thisProjectRef, version) { (id, v) => _ => "orange:%s:%s> ".format(id.project, v)}
  )

  lazy val scalacSettings = Seq[Setting[_]](
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    scalacOptions += "-feature",
    scalacOptions += "-language:implicitConversions",
    scalacOptions += "-language:higherKinds",
    scalacOptions += "-language:existentials",
    scalacOptions += "-language:postfixOps",
    scalacOptions += "-Xfatal-warnings")

  lazy val macroSettings = Seq[Setting[_]](
    libraryDependencies ++= Libs.macrosQuasi,
    addCompilerPlugin(Libs.macrosPlugin))

  object Integration {
    def isIntegrationTest(name: String): Boolean = name endsWith "IT"
    def isUnitTest(name: String): Boolean = !isIntegrationTest(name)
    lazy val cfg      = config("it") extend Test
    lazy val settings = inConfig(Integration.cfg)(Defaults.testTasks :+
      (testOptions in Test := Seq(Tests.Filter(Integration.isUnitTest))) :+
      (testOptions in Integration.cfg := Seq(Tests.Filter(Integration.isIntegrationTest))))
  }

  object Assembly {
    def settings: Seq[Setting[_]] =
      Seq[Setting[_]](sbtassembly.Plugin.assemblySettings: _*) ++
        Seq[Setting[_]](test in AssemblyKeys.assembly := {}) ++
        Seq[Setting[_]](addArtifact(Artifact("apps", "assembly"), AssemblyKeys.assembly): _*)
  }

  def getBasePackageName(projectName: String, suffix: String = null) = {
    val root = "cgta"
    val name = Option(suffix).map(projectName.split(_)(0)).getOrElse(projectName)
    root + "." + name
  }


  def sjsProject(name: String, p: Project) = {
    //Working around bugs in scala atm
//    def fixPhantomJsSetting = {
//      import scala.collection.JavaConverters._
//      import scala.scalajs.sbtplugin.ScalaJSPlugin._
//      import scala.scalajs.sbtplugin.env.nodejs.NodeJSEnv
//      import scala.scalajs.sbtplugin.env.phantomjs.PhantomJSEnv
//      val env = System.getenv().asScala.toList.map { case (k, v) => s"$k=$v"}
//      ScalaJSKeys.postLinkJSEnv := {if (ScalaJSKeys.requiresDOM.value) new PhantomJSEnv(None, env) else new NodeJSEnv}
//    }

    p.settings(basicSettings ++ ScalaJSPlugin.scalaJSSettings: _*)
      .settings(scalacOptions += "-language:reflectiveCalls")
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name, "-sjs")))
      .settings(Deps.Otest.settingsSjs : _*)
      .settings(cgta.otest.OtestPlugin.settingsSjs: _ *)
      .settings(Publish.settings: _*)
      .settings(ReleasePlugin.releaseSettings: _*)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
      .settings(test in Test := (test in(Test, ScalaJSKeys.fastOptStage)).value)
      .settings(testOnly in Test := (testOnly in(Test, ScalaJSKeys.fastOptStage)).evaluated)
      .settings(testQuick in Test := (testQuick in(Test, ScalaJSKeys.fastOptStage)).evaluated)
//      .settings(fixPhantomJsSetting)
  }

  def jvmProject(name: String, p: Project) = {
    p.settings(basicSettings: _*)
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name, "-jvm")))
      .settings(Deps.Otest.settingsJvm: _*)
      .settings(cgta.otest.OtestPlugin.settingsJvm: _ *)
      .configs(Integration.cfg)
      .settings(Integration.settings: _*)
      .settings(Publish.settings: _*)
      .settings(ReleasePlugin.releaseSettings: _*)
      .settings(JacocoPlugin.jacoco.settings: _*)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
      .settings(
        unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "java",
        unmanagedSourceDirectories in Test += baseDirectory.value / ".." / ".." / "test" / "java"
      )
  }

  def crossProject(name: String) = XSjsProjects(name, file(name))
    .settingsBase(basicSettings: _*)
    .settingsBase(Deps.Otest.settingsJvm: _*)
    .settingsBase(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name)))
    .settingsBase(test := {})
    .settingsBase(testQuick := {})
    .settingsBase(testOnly := {})
    .settingsBase(compile := {sbt.inc.Analysis.Empty})
    .mapJvm(jvmProject(name, _))
    .mapSjs(sjsProject(name, _))

  def playProject(name: String, sjsProjects: Seq[sbt.ProjectReference]) = {
    lazy val sjsForPlayOutDir = Def.settingKey[File]("directory for javascript files output by scalajs")
    lazy val lastSjsProject = sjsProjects.last

    lazy val sjsTasks = List(ScalaJSKeys.packageExternalDepsJS,
      ScalaJSKeys.packageInternalDepsJS,
      ScalaJSKeys.packageExportedProductsJS,
      ScalaJSKeys.fastOptJS,
      ScalaJSKeys.fullOptJS)

    play.Project("playweb", "1.0.0", Deps.Play.exclusiveDeps, file("playweb"))
      .aggregate(sjsProjects: _*)
      .settings(publish := {})
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name)))
      .settings(play.Project.playScalaSettings: _*)
      .settings(com.jamesward.play.BrowserNotifierPlugin.livereload: _*)
      .settings(sjsForPlayOutDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
        compile in Compile <<= (compile in Compile) dependsOn (ScalaJSKeys.fastOptJS in(lastSjsProject, Compile)),
        dist <<= dist dependsOn (ScalaJSKeys.fullOptJS in(lastSjsProject, Compile))
//        EclipseKeys.skipParents in ThisBuild := false
      )
      .settings()
      .settings(sjsTasks.map(t => crossTarget in(lastSjsProject, Compile, t) := sjsForPlayOutDir.value): _*)
  }


}