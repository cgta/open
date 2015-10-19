import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import spray.revolver.RevolverPlugin.Revolver
//import org.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys


object BaseBuild extends Build {
  //  sys.props("scalac.patmat.analysisBudget") = "512"
  //org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(ch.qos.logback.classic.Level.INFO)


  lazy val basicSettings = repos ++
    scalacSettings ++
    promptSettings ++
    noScaladocSettings

  lazy val repos = Seq[Setting[_]](
    resolvers += Resolver.url("Typesafe Ivy Releases", url("https://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns),
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val noScaladocSettings = Seq[Setting[_]](publishArtifact in(Compile, packageDoc) := Publish.includeScaladoc)

  lazy val promptSettings = Seq[Setting[_]](
    shellPrompt <<= (thisProjectRef, version) { (id, v) => _ => "ultimate:%s:%s> ".format(id.project, v) }
  )

  lazy val scalacSettings = Seq[Setting[_]](
    scalaVersion := "2.11.7",
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

    lazy val cfg = config("it") extend Test
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

  def xjs(name: String, p: Project): Project = p
    .settings(basicSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .settings(scalacOptions += "-language:reflectiveCalls")
    .settings(Libs.cgtaOtest.settingsSjs: _*)
    .settings(Publish.settingsSjs: _*)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  def xjvm(name: String, p: Project): Project = p
    .settings(basicSettings: _*)
    .settings(testFrameworks := Nil)
    .settings(Libs.cgtaOtest.settingsJvm: _*)
    .configs(Integration.cfg)
    .settings(Integration.settings: _*)
    .settings(Publish.settingsJvm: _*)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .settings(Revolver.settings :+ LoopMain.loopMain: _*)

  def xp(name: String, p: CrossProject): CrossProject = p
    .settings(basicSettings: _*)
    .jsConfigure(xjs(name, _))
    .jvmConfigure(xjvm(name, _))


  val cdpSettings: Seq[Def.Setting[_]] =
    Seq[Def.Setting[_]](
      scalacOptions += "-language:reflectiveCalls",
      publishArtifact in Test := false,
      parallelExecution in Test := false)

  def cdpXp(name: String, p: CrossProject): CrossProject = xp(name, p).jvmSettings(cdpSettings: _*)

  def cdpProject(name: String) = xjvm(name, Project(name, file(name))).settings(cdpSettings: _*)

}
