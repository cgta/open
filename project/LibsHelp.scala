import org.scalajs.sbtplugin.cross.CrossProject
import sbt._

object LibsHelp {

  object Implicits {

    implicit class CrossProjectExtensions(val p: CrossProject) extends AnyVal {
      def addLib(d: JvmLib): CrossProject = p.jvmSettings(d.settings: _*)
      def addLib(d: SjsLib): CrossProject = p.jsSettings(d.settings: _*)
      def addLib(d: CrossLib): CrossProject = p.configure(d.mapCross)
      def addLibs(ds: Lib*): CrossProject = ds.foldLeft(p) {
        case (p: CrossProject, d: JvmLib) => p addLib d
        case (p: CrossProject, d: SjsLib) => p addLib d
        case (p: CrossProject, d: CrossLib) => p addLib d
      }
    }

  }

  sealed trait Lib

  trait JvmLib extends Lib {
    def settings: Seq[Setting[_]]
  }

  def jvmLib(settings: Seq[Setting[_]]) = {
    val lsettings = settings
    new JvmLib {
      override def settings: Seq[Setting[_]] = lsettings
    }
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

    def mapCross(p: CrossProject): CrossProject = {
      //      p.settingsBase(settingsJvm: _*) //Include the jvm version of the library in the base package for intellij
      //        //p.settingsBase(settingsSjs: _*) //Include the sjs version of the library in the base package for intellij
      //        .settingsSjs(settingsSjs: _*)
      //        .settingsJvm(settingsJvm: _*)
      p.jsSettings(settingsSjs: _*).jvmSettings(settingsJvm: _*)
    }
  }

  def crossLib(
    base: Seq[Setting[_]] = Nil,
    jvm: Seq[Setting[_]] = Nil,
    sjs: Seq[Setting[_]] = Nil
  ) = {
    val lbase = base
    val ljvm = jvm
    val lsjs = sjs
    new CrossLib {
      override protected def base: Seq[Setting[_]] = lbase
      override protected def jvm: Seq[Setting[_]] = ljvm
      override protected def sjs: Seq[Setting[_]] = lsjs
    }
  }
}