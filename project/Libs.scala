import sbt.Keys._
import sbt._

//Eventually this is to replace the libs
object Libs {
  import LibsHelp._

  type S = Seq[Setting[_]]
  def S(s: Setting[_]*): S = Seq[Setting[_]](s : _*)

  case object Reflect extends JvmLib {
    override lazy val settings = S(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)
  }

  case object Otest extends CrossLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

    lazy          val otestVersion = SettingKey[String]("otest-version")
    override lazy val base         = S(otestVersion := "0.2.1")
    override lazy val jvm          = S(
      libraryDependencies += "biz.cgta" %% "otest-jvm" % otestVersion.value,
      testFrameworks := Seq(new TestFramework("cgta.otest.runner.OtestSbtFramework"))
    )
    override lazy val sjs          = S(
      libraryDependencies += "biz.cgta" %%%! "otest-sjs" % otestVersion.value,
      testFrameworks := Seq(new TestFramework("cgta.otest.runner.OtestSbtFramework")),
      scalaJSStage in Test := FastOptStage
    )
  }

  case object Autowire extends CrossLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

    lazy          val autowireVersion = SettingKey[String]("autowire-version")
    override lazy val base            = S(autowireVersion := "0.2.9")
    override lazy val jvm             = S(libraryDependencies += "biz.cgta" %% "autowire" % autowireVersion.value)
    override lazy val sjs             = S(libraryDependencies += "biz.cgta" %%%! "autowire" % autowireVersion.value)
  }

  case object Async extends CrossLib {
    lazy          val asyncVersion = SettingKey[String]("async-version")
    override lazy val base         = S(
      asyncVersion := "0.9.1",
      libraryDependencies += "org.scala-lang.modules" %% "scala-async" % asyncVersion.value
    )
    override protected def jvm: Seq[Setting[_]] = Nil
    override protected def sjs: Seq[Setting[_]] = Nil
  }

  case object Time extends CrossLib {

    case object MomentJs extends SjsLib {
      import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
      lazy          val momentJsVersion = SettingKey[String]("moment-js-version")
      override lazy val settings        = S(
        momentJsVersion := "2.7.0",
        libraryDependencies += "org.webjars" % "momentjs" % momentJsVersion.value,
        jsDependencies += "org.webjars" % "momentjs" % momentJsVersion.value / "moment.js"
      )
    }

    case object Joda extends JvmLib {
      lazy          val jodaVersion        = SettingKey[String]("joda-version")
      lazy          val jodaConvertVersion = SettingKey[String]("joda-convert-version")
      override lazy val settings           = S(
        jodaVersion := "2.3",
        jodaConvertVersion := "1.2",
        libraryDependencies += "joda-time" % "joda-time" % jodaVersion.value,
        libraryDependencies += "org.joda" % "joda-convert" % jodaConvertVersion.value
      )
    }

    override lazy val base: S = Nil
    override protected def jvm = Joda.settings
    override protected def sjs = MomentJs.settings
  }


  case object Mongo extends JvmLib {
    lazy          val mongoVersion = SettingKey[String]("mongo-version")
    override lazy val settings     = S(
      mongoVersion := "2.0.7",
      libraryDependencies += "org.mongodb" % "mongo-java-driver" % mongoVersion.value
    )
  }

  case object Mysql extends JvmLib {
    lazy          val mysqlVersion = SettingKey[String]("mysql-version")
    override lazy val settings     = S(
      mysqlVersion := "5.1.34",
      libraryDependencies += "mysql" % "mysql-connector-java" % mysqlVersion.value
    )
  }

  case object AmazonAws extends JvmLib {
    lazy          val amazonAwsVersion = SettingKey[String]("amazon-aws-version")
    override lazy val settings         = S(
      amazonAwsVersion := "1.9.9",
      libraryDependencies += "com.amazonaws" % "aws-java-sdk" % amazonAwsVersion.value
    )
  }


  case object Dom extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy          val domVersion = SettingKey[String]("dom-version")
    override lazy val settings   = S(
      domVersion := "0.8.0",
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % domVersion.value
    )
  }

  case object JQuery extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy          val jqueryVersion = SettingKey[String]("jquery-version")
    override lazy val settings      = S(
      jqueryVersion := "0.7.0",
      libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % jqueryVersion.value
    )
  }


  case object ScalaJsReact extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy          val scalaJsReactVersion = SettingKey[String]("scala-js-react-version")
    override lazy val settings            = S(
      scalaJsReactVersion := "0.8.0",
      libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion.value
    )
  }

    case object Scalatags extends SjsLib {
      import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
      val scalatagsVersion = SettingKey[String]("scalatags-version")
      override val settings=S(
        scalatagsVersion := "0.4.5",
        libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion.value
      )
    }


  case object Akka extends JvmLib {
    lazy          val akkaVersion                     = SettingKey[String]("akka-version")
    lazy          val akkaReactiveExperimentalVersion = SettingKey[String]("akka-reactive-experimental-version")
    override lazy val settings: Seq[Setting[_]]       = S(
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
    lazy          val sprayVersion = SettingKey[String]("spray-version")
    override lazy val settings     = S(
      sprayVersion := "1.3.2",
      libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion.value,
      libraryDependencies += "io.spray" %% "spray-client" % sprayVersion.value
    )
  }

  case object Jedis extends JvmLib {
    lazy          val jedisVersion = SettingKey[String]("jedis-version")
    override lazy val settings     = S(
      jedisVersion := "2.6.0",
      libraryDependencies += "redis.clients" % "jedis" % jedisVersion.value
    )
  }

  case object Protobuf extends JvmLib {
    lazy          val protobufVersion = SettingKey[String]("protobuf-version")
    override lazy val settings        = S(
      protobufVersion := "2.6.0",
      libraryDependencies += "com.google.protobuf" % "protobuf-java" % protobufVersion.value
    )
  }

//  case object ScalaSsh extends JvmLib {
//    lazy          val scalaSshVersion           = SettingKey[String]("scala-ssh-version")
//    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
//      scalaSshVersion := "0.7.0",
//      libraryDependencies += "com.decodified" %% "scala-ssh" % "0.7.0",
//      libraryDependencies += "org.bouncycastle" % "bcprov-jdk16" % "1.46",
//      libraryDependencies += "com.jcraft" % "jzlib" % "1.1.3"
//    )
//  }

  case object SbtIO extends JvmLib{
    lazy          val sbtVersion              = SettingKey[String]("sbt-version")
    override lazy val settings = S(
      sbtVersion := "0.13.6",
      libraryDependencies += {
        if (scalaVersion.value.startsWith("2.10.")) {
          "org.scala-sbt" % "io" % sbtVersion.value
        } else {
          "org.scala-sbt" %% "io" % sbtVersion.value
        }
      }
    )
  } 
     
  case object TwitterLibs extends JvmLib {
    // lazy          val twitterLibsVersion        = SettingKey[String]("twitter-libs-version")
    // override lazy val settings=S(
    //   twitterLibsVersion := "6.22.0",
    //   libraryDependencies += "com.twitter" %% "util-eval" % twitterLibsVersion.value,
    //   libraryDependencies += "com.twitter" %% "finagle-http" % twitterLibsVersion.value
    // )
    override lazy val settings = S()
  }

  case object ApacheCommons extends JvmLib {
    override lazy val settings     = S(
      libraryDependencies += "commons-io" % "commons-io" %  "2.4",
      libraryDependencies += "commons-lang" % "commons-lang" % "2.4",
      libraryDependencies += "commons-net" % "commons-net" % "3.3",
      libraryDependencies += "org.apache.commons" % "commons-compress" % "1.9"
    )
  }
   

  //  case object Spark extends Dep {
  //    val sparkVersion = SettingKey[String]("spark-version")
  //    override val settings=S(
  //      sparkVersion := "1.0.2",
  //      libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion.value
  //      //        exclude ("log4j", "log4j")
  //      //        exclude ("org.slf4j", "slf4j-api")
  //      //        exclude ("org.slf4j", "slf4j-log4j12")
  //    )
  //  }


  //  case object ScalikeJDBC extends Dep {
  //    val scalikeJDBCVersion = SettingKey[String]("scalikejdbc-version")
  //    override val settings=S(
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
  //    override val settings=S(
  //      scalatagsVersion := "0.4.0",
  //      libraryDependencies += "com.scalatags" %%% "scalatags" % scalatagsVersion.value
  //    )
  //  }


  //  case object Scalalang extends Dep {
  //    val scalaCompilerVersion = SettingKey[String]("scala-compiler-version")
  //    override val settings=S(
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
