import sbt.Keys._
import sbt._

//Eventually this is to replace the libs
object Deps {

  trait Dep {
    val settings: Seq[Setting[_]]
  }

  trait CrossDep {
    protected val base: Seq[Setting[_]]
    protected val jvm : Seq[Setting[_]]
    protected val sjs : Seq[Setting[_]]

    final def settingsJvm = base ++ jvm
    final def settingsSjs = base ++ sjs
  }

  case object MomentJs extends Dep {
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    val momentJsVersion = SettingKey[String]("moment-js-version")
    override val settings = Seq[Setting[_]](
      momentJsVersion := "2.7.0",
      libraryDependencies += "org.webjars" % "momentjs" % momentJsVersion.value,
      ScalaJSKeys.jsDependencies += "org.webjars" % "momentjs" % momentJsVersion.value / "moment.js"
    )
  }

  case object Reflect extends Dep {
    override val settings = Seq[Setting[_]](
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  }

  case object Otest extends CrossDep {
    val otestVersion = SettingKey[String]("otest-version")

    override val base: Seq[Setting[_]] = Seq[Setting[_]](
      //Remember to set in the otestSbtPlugin.sbt file!!
      otestVersion := "0.1.11"
    )

    override val jvm: Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "biz.cgta" %% "otest-jvm" % otestVersion.value
    )
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    override val sjs: Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "biz.cgta" %%% "otest-sjs" % otestVersion.value
    )
  }


  case object Play extends Dep {
    //    val play              = List("com.typesafe.play" %% "play" % Versions.play)
    //    val playJson          = List("com.typesafe.play" %% "play-json" % Versions.play)
    val playVersion = SettingKey[String]("play-version")

    val shared = Seq[Setting[_]](playVersion := "2.2.1")

    override val settings: Seq[Setting[_]] = shared ++ Seq[Setting[_]](
      libraryDependencies += "com.typesafe.play" %% "play" % playVersion.value
    )

    val jsonOnlySettings: Seq[Setting[_]] = shared ++ Seq[Setting[_]](
      libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion.value
    )

    import play.{Project => PlayProject}
    val exclusiveDeps = Seq(
      PlayProject.filters,
      PlayProject.jdbc,
      "ch.qos.logback" % "logback-classic" % "1.0.13" exclude("org.slf4j", "slf4j-nop"),
      "org.forgerock.opendj" % "opendj-ldap-sdk" % "2.6.7"
    )
  }

  case object Spark extends Dep {
    val sparkVersion = SettingKey[String]("spark-version")
    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
      sparkVersion := "1.0.2",
      libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion.value
      //        exclude ("log4j", "log4j")
      //        exclude ("org.slf4j", "slf4j-api")
      //        exclude ("org.slf4j", "slf4j-log4j12")
    )
  }

  case object ScalikeJDBC extends Dep {
    val scalikeJDBCVersion = SettingKey[String]("scalikejdbc-version")
    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
      scalikeJDBCVersion := "2.0.7",
      libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % scalikeJDBCVersion.value
    )
  }


  case object Mongo extends Dep {
    val mongoVersion = SettingKey[String]("mongo-version")
    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
      mongoVersion := "2.0.7",
      libraryDependencies += "org.mongodb" % "mongo-java-driver" % mongoVersion.value
    )
  }

  //  case object Logback extends Dep {
  //    val logbackVersion = SettingKey[String]("logback-version")
  //
  //    def logbackModule(module: String, version: String) = "ch.qos.logback" % ("logback-" + module) % version
  //
  //    val settings: Seq[Setting[_]] = Seq(
  //      logbackVersion := "1.0.13",
  //      libraryDependencies ++= Seq(
  //        logbackModule("core", logbackVersion.value),
  //        logbackModule("classic", logbackVersion.value)
  //      )
  //    )
  //  }
}
