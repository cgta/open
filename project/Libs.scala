import cgta.sbtxsjs.SbtXSjsPlugin.XSjsProjects
import sbt.Keys._
import sbt._

object LibsHelp {
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

//Eventually this is to replace the libs
object Libs {
  import LibsHelp._


  case object Reflect extends JvmLib {
    override lazy val settings = Seq[Setting[_]](
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  }

  case object Otest extends CrossLib {
    lazy          val otestVersion          = SettingKey[String]("otest-version")
    override lazy val base: Seq[Setting[_]] = Seq[Setting[_]](
      //Remember to set in the otestSbtPlugin.sbt file!!
      otestVersion := "0.1.14"
    )
    override lazy val jvm : Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "biz.cgta" %% "otest-jvm" % otestVersion.value
    )
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    override lazy val sjs: Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "biz.cgta" %%% "otest-sjs" % otestVersion.value
    )
  }

  case object Autowire extends CrossLib {
    lazy          val autowireVersion       = SettingKey[String]("autowire-version")
    override lazy val base: Seq[Setting[_]] = Seq[Setting[_]](
      //Remember to set in the otestSbtPlugin.sbt file!!
      autowireVersion := "0.2.3"
    )
    override lazy val jvm : Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "com.lihaoyi" %% "autowire" % autowireVersion.value
    )
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    override lazy val sjs: Seq[Setting[_]] = Seq[Setting[_]](
      libraryDependencies += "com.lihaoyi" %%% "autowire" % autowireVersion.value
    )
  }

  case object Async extends CrossLib {
    lazy          val asyncVersion          = SettingKey[String]("async-version")
    override lazy val base: Seq[Setting[_]] = Seq[Setting[_]](
      asyncVersion := "0.9.1",
      libraryDependencies += "org.scala-lang.modules" %% "scala-async" % asyncVersion.value
    )
    override protected def jvm: Seq[Setting[_]] = Nil
    override protected def sjs: Seq[Setting[_]] = Nil
  }

  case object Time extends CrossLib {
    override lazy val base: Seq[Setting[_]] = Nil
    override protected def jvm: Seq[Setting[_]] = Libs.Joda.settings
    override protected def sjs: Seq[Setting[_]] = Libs.MomentJs.settings
  }

  case object MomentJs extends SjsLib {
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    lazy          val momentJsVersion = SettingKey[String]("moment-js-version")
    override lazy val settings        = Seq[Setting[_]](
      momentJsVersion := "2.7.0",
      libraryDependencies += "org.webjars" % "momentjs" % momentJsVersion.value,
      ScalaJSKeys.jsDependencies += "org.webjars" % "momentjs" % momentJsVersion.value / "moment.js"
    )
  }

  case object Joda extends JvmLib {
    lazy          val jodaVersion        = SettingKey[String]("joda-version")
    lazy          val jodaConvertVersion = SettingKey[String]("joda-convert-version")
    override lazy val settings           = Seq[Setting[_]](
      jodaVersion := "2.3",
      jodaConvertVersion := "1.2",
      libraryDependencies += "joda-time" % "joda-time" % jodaVersion.value,
      libraryDependencies += "org.joda" % "joda-convert" % jodaConvertVersion.value
    )
  }


  case object Mongo extends JvmLib {
    lazy          val mongoVersion              = SettingKey[String]("mongo-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      mongoVersion := "2.0.7",
      libraryDependencies += "org.mongodb" % "mongo-java-driver" % mongoVersion.value
    )
  }

  case object AmazonAws extends JvmLib {
    lazy          val amazonAwsVersion          = SettingKey[String]("amazon-aws-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      amazonAwsVersion := "1.8.11",
      libraryDependencies += "com.amazonaws" % "aws-java-sdk" % amazonAwsVersion.value
    )

  }


  case object Dom extends SjsLib {
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    lazy          val domVersion                = SettingKey[String]("dom-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      domVersion := "0.6",
      libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % domVersion.value
    )
  }

  case object JQuery extends SjsLib {
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    lazy          val jqueryVersion             = SettingKey[String]("jquery-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      jqueryVersion := "0.6",
      libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % jqueryVersion.value
    )
  }


  case object ScalaJsReact extends SjsLib {
    import scala.scalajs.sbtplugin.ScalaJSPlugin._
    lazy          val scalaJsReactVersion       = SettingKey[String]("scala-js-react-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      scalaJsReactVersion := "0.6.1",
      libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion.value
    )
  }


  case object TwitterLibs extends JvmLib {
    // lazy          val twitterLibsVersion        = SettingKey[String]("twitter-libs-version")
    // override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
    //   twitterLibsVersion := "6.22.0",
    //   libraryDependencies += "com.twitter" %% "util-eval" % twitterLibsVersion.value,
    //   libraryDependencies += "com.twitter" %% "finagle-http" % twitterLibsVersion.value
    // )
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]]()
  }


  case object Akka extends JvmLib {
    lazy          val akkaVersion                     = SettingKey[String]("akka-version")
    lazy          val akkaReactiveExperimentalVersion = SettingKey[String]("akka-reactive-experimental-version")
    override lazy val settings: Seq[Setting[_]]       = Seq[Setting[_]](
      //      akkaVersion := "2.2.4",
      akkaVersion := "2.3.6",
      akkaReactiveExperimentalVersion := "0.9",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion.value,
      libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVersion.value,
      libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaVersion.value
      //      ,
      //      libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaReactiveExperimentalVersion.value,
      //      libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaReactiveExperimentalVersion.value
    )
  }

  case object Play extends JvmLib {
    //    val play              = List("com.typesafe.play" %% "play" % Versions.play)
    //    val playJson          = List("com.typesafe.play" %% "play-json" % Versions.play)
    lazy          val playVersion                       = SettingKey[String]("play-version")
    lazy          val shared                            = Seq[Setting[_]](
      //!!!!!! CHANGE VERSION IN PLUGINS! TOO !!!!!
      playVersion := "2.3.6"
    )
    override lazy val settings        : Seq[Setting[_]] = shared ++ Seq[Setting[_]](
      libraryDependencies += "com.typesafe.play" %% "play" % playVersion.value
    )
    lazy          val jsonOnlySettings: Seq[Setting[_]] = shared ++ Seq[Setting[_]](
      libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion.value
    )

    lazy val exclusiveDeps = Seq(
      play.Play.autoImport.filters,
      play.Play.autoImport.jdbc,
      "ch.qos.logback" % "logback-classic" % "1.0.13" exclude("org.slf4j", "slf4j-nop"),
      "org.forgerock.opendj" % "opendj-ldap-sdk" % "2.6.7"
    )
  }


  case object Spray extends JvmLib {
    lazy          val sprayVersion              = SettingKey[String]("spray-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      sprayVersion := "1.3.2",
      libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion.value,
      libraryDependencies += "io.spray" %% "spray-client" % sprayVersion.value
    )
  }

  case object Protobuf extends JvmLib {
    lazy val protobufVersion = SettingKey[String]("protobuf-version")
    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
      protobufVersion := "2.6.0",
      libraryDependencies += "com.google.protobuf" % "protobuf-java" % protobufVersion.value
    )
  }


  //  case object Spark extends Dep {
  //    val sparkVersion = SettingKey[String]("spark-version")
  //    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
  //      sparkVersion := "1.0.2",
  //      libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion.value
  //      //        exclude ("log4j", "log4j")
  //      //        exclude ("org.slf4j", "slf4j-api")
  //      //        exclude ("org.slf4j", "slf4j-log4j12")
  //    )
  //  }


  //  case object ScalikeJDBC extends Dep {
  //    val scalikeJDBCVersion = SettingKey[String]("scalikejdbc-version")
  //    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
  //      scalikeJDBCVersion := "2.0.7",
  //      libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % scalikeJDBCVersion.value
  //    )
  //  }


  //  object sjs {
  //    import scala.scalajs.sbtplugin.ScalaJSPlugin._
  //    val dom = Seq("org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6")
  //    val jquery    = Seq("org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6")
  //    val scalatags = Seq("com.scalatags" %%% "scalatags" % "0.3.8")
  //  }


  //  case object Scalatags extends Dep {
  //    import scala.scalajs.sbtplugin.ScalaJSPlugin._
  //    val scalatagsVersion = SettingKey[String]("scalatags-version")
  //    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
  //      scalatagsVersion := "0.4.0",
  //      libraryDependencies += "com.scalatags" %%% "scalatags" % scalatagsVersion.value
  //    )
  //  }


  //  case object Scalalang extends Dep {
  //    val scalaCompilerVersion = SettingKey[String]("scala-compiler-version")
  //    override val settings: Seq[Setting[_]] = Seq[Setting[_]](
  //      scalaCompilerVersion := scalaVersion.value,
  //      libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaCompilerVersion.value
  //    )
  //  }

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
