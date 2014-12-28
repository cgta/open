import LibsHelp.{Lib, CrossLib, JvmLib, SjsLib}
import cgta.sbtxsjs.SbtXSjsPlugin.XSjsProjects
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import de.johoop.jacoco4sbt.JacocoPlugin
import org.sbtidea.SbtIdeaPlugin
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys
import sbtrelease.ReleasePlugin

import scala.scalajs.sbtplugin.ScalaJSPlugin
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.web.Import.Assets


object BaseBuild extends Build with UniversalKeys {
  sys.props("scalac.patmat.analysisBudget") = "512"
  //org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(ch.qos.logback.classic.Level.INFO)

  implicit class XSjsProjectsExtensions(val p: XSjsProjects) extends AnyVal {
    def addLib(d: JvmLib): XSjsProjects = p.settingsJvm(d.settings: _*)
    def addLib(d: SjsLib): XSjsProjects = p.settingsSjs(d.settings: _*)
    def addLib(d: CrossLib): XSjsProjects = p.mapSelf(d.mapCross)
    def addLibs(ds: Lib*): XSjsProjects = ds.foldLeft(p) {
      case (p: XSjsProjects, d: JvmLib) => p addLib d
      case (p: XSjsProjects, d: SjsLib) => p addLib d
      case (p: XSjsProjects, d: CrossLib) => p addLib d
    }
  }


  lazy val basicSettings = scalacSettings ++
    promptSettings ++
    noScaladocSettings

  lazy val noScaladocSettings = Seq[Setting[_]](publishArtifact in(Compile, packageDoc) := false)

  lazy val promptSettings = Seq[Setting[_]](
    shellPrompt <<= (thisProjectRef, version) { (id, v) => _ => "orange:%s:%s> ".format(id.project, v)}
  )

  lazy val scalacSettings = Seq[Setting[_]](
    scalaVersion := "2.11.4",
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    scalacOptions += "-feature",
    scalacOptions += "-language:implicitConversions",
    scalacOptions += "-language:higherKinds",
    scalacOptions += "-language:existentials",
    scalacOptions += "-language:postfixOps",
    scalacOptions += "-Xfatal-warnings"
    )



 lazy val macroSettings = libraryDependencies ++= (if (scalaVersion.value.startsWith("2.11")) {
    Nil
  } else {
    Seq(
      "org.scalamacros" %% "quasiquotes" % "2.0.0",
      compilerPlugin("org.scalamacros" %% "paradise" % "2.0.0" cross CrossVersion.full)
    )
  })
  

  

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
    p.settings(basicSettings ++ ScalaJSPlugin.scalaJSSettings: _*)
      .settings(scalacOptions += "-language:reflectiveCalls")
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name, "-sjs")))
      .settings(Libs.Otest.settingsSjs: _*)
      .settings(cgta.otest.OtestPlugin.settingsSjs: _ *)
      .settings(Publish.settingsSjs: _*)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
      .settings(test in Test := (test in(Test, ScalaJSKeys.fastOptStage)).value)
      .settings(testOnly in Test := (testOnly in(Test, ScalaJSKeys.fastOptStage)).evaluated)
      .settings(testQuick in Test := (testQuick in(Test, ScalaJSKeys.fastOptStage)).evaluated)
  }

  def jvmProject(name: String, p: Project) = {
    p.settings(basicSettings: _*)
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name, "-jvm")))
      .settings(Libs.Otest.settingsJvm: _*)
      .settings(testFrameworks := Nil)
      .settings(cgta.otest.OtestPlugin.settingsJvm: _ *)
      .configs(Integration.cfg)
      .settings(Integration.settings: _*)
      .settings(Publish.settingsJvm: _*)
      .settings(JacocoPlugin.jacoco.settings: _*)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
      .settings(
        unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "java",
        unmanagedSourceDirectories in Test += baseDirectory.value / ".." / ".." / "test" / "java"
      )
  }

  def crossProject(name: String) = XSjsProjects(name, file(name))
    .settingsBase(basicSettings: _*)
    .settingsBase(Libs.Otest.settingsJvm: _*)
    .settingsBase(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name)))
    .settingsBase(test := {})
    .settingsBase(testQuick := {})
    .settingsBase(testOnly := {})
    .settingsBase(compile := {sbt.inc.Analysis.Empty})
    .mapJvm(jvmProject(name, _))
    .mapSjs(sjsProject(name, _))
    .settingsJvmTest(basicSettings: _*)
    .settingsSjsTest(basicSettings: _*)


  val cdpSettings: Seq[Def.Setting[_]] =
    Seq[Def.Setting[_]](
      scalacOptions += "-language:reflectiveCalls",
      publishArtifact in Test := false,
      parallelExecution in Test := false)

  def cdpProject(name: String) = jvmProject(name, Project(name, file(name))).settings(cdpSettings: _*)

  def cdpCrossProject(name: String, newName: String) = {
    var p = crossProject(name).settingsJvm(cdpSettings: _*)
    if (newName.nonEmpty) {
      p = p.settingsSjs(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(newName, "-sjs")))
      p = p.settingsJvm(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(newName, "-jvm")))
    }
    p
  }

  def playProject(name: String, sjsProjects: Seq[sbt.ProjectReference]) = {
    lazy val sjsForPlayOutDir = Def.settingKey[File]("directory for javascript files output by scalajs")
    lazy val lastSjsProject = sjsProjects.last

    lazy val sjsTasks = List(
//      ScalaJSKeys.packageExternalDepsJS,
//      ScalaJSKeys.packageInternalDepsJS,
//      ScalaJSKeys.packageExportedProductsJS,
      ScalaJSKeys.fastOptJS,
      ScalaJSKeys.fullOptJS)

    Project("playweb", file("playweb"))
      .enablePlugins(play.PlayScala, SbtWeb)
      .settings(libraryDependencies ++= Libs.Play.exclusiveDeps)
      .aggregate(sjsProjects: _*)
      .settings(publish := {})
      .settings(SbtIdeaPlugin.ideaBasePackage := Some(getBasePackageName(name)))
      //      .settings(play.Project.playScalaSettings: _*)
      //      .settings(com.jamesward.play.BrowserNotifierPlugin.livereload: _*)
      .settings(basicSettings: _*)
      .settings(sjsForPlayOutDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
        compile in Compile <<= (compile in Compile) dependsOn (ScalaJSKeys.fastOptJS in(lastSjsProject, Compile)),
        includeFilter in(Assets, LessKeys.less) := "*.less",
        dist <<= dist dependsOn (ScalaJSKeys.fullOptJS in(lastSjsProject, Compile))
      )
      .settings()
      .settings(sjsTasks.map(t => crossTarget in(lastSjsProject, Compile, t) := sjsForPlayOutDir.value): _*)
  }


}