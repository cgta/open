import cgta.sbtxsjs.SbtXSjsPlugin.XSjsProjects
import sbt._

object LibsHelp {

  object Implicits {

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
  }

  sealed trait Lib

  trait JvmLib extends Lib {
    def settings: Seq[Setting[_]]
  }

  trait SjsLib extends Lib {
    def settings: Seq[Setting[_]]
  }

  trait CrossLib extends Lib {
    protected def base: Seq[Setting[_]]
    protected def jvm: Seq[Setting[_]]
    protected def sjs: Seq[Setting[_]]
    final def settingsJvm = base ++ jvm
    final def settingsSjs = base ++ sjs

    def mapCross(p: XSjsProjects): XSjsProjects = {
      p.settingsBase(settingsJvm: _*) //Include the jvm version of the library in the base package for intellij
        //p.settingsBase(settingsSjs: _*) //Include the sjs version of the library in the base package for intellij
        .settingsSjs(settingsSjs: _*)
        .settingsJvm(settingsJvm: _*)
    }
  }
}