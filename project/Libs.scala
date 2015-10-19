import sbt.Keys._
import sbt._

//Eventually this is to replace the libs
object Libs {
  import LibsHelp._
  import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

  type S = Seq[Setting[_]]
  def S(s: Setting[_]*): S = Seq[Setting[_]](s: _*)

  def L(ms: ModuleID*) = S(libraryDependencies ++= ms)

  //A list of libraries
  object Old {
    val slf4jForTests = List(
      "org.slf4j" % "slf4j-api" % "1.5.6" % "test",
      "org.slf4j" % "slf4j-nop" % "1.5.6" % "test")

    val openFast = List("org.openfast" % "openfast" % "1.1.1")

    val wekaAll = Seq(
      "nz.ac.waikato.cms.weka" % "weka-dev" % "3.7.11"
      //    ,"nz.ac.waikato.cms.weka" % "RBFNetwork" % "1.0.7"
    )


    object CdpOnly {


      val slf4jApi = List("org.slf4j" % "slf4j-api" % "1.6.6")
      val jetty = List("org.eclipse.jetty.aggregate" % "jetty-all" % "8.0.1.v20110908")

      val disruptor = List("com.lmax" % "disruptor" % "3.1.1")
    }
  }

  //##########################################################################################
  //CROSS DEPS!
  //##########################################################################################


  lazy val cgtaOtest = {
    val OtestVersion = "0.2.1"
    crossLib(
      jvm = S(
        libraryDependencies += "biz.cgta" %% "otest-jvm" % OtestVersion,
        testFrameworks := Seq(new TestFramework("cgta.otest.runner.OtestSbtFramework"))
      ),
      sjs = S(
        libraryDependencies += "biz.cgta" %%%! "otest-sjs" % OtestVersion,
        testFrameworks := Seq(new TestFramework("cgta.otest.runner.OtestSbtFramework")),
        scalaJSStage in Test := FastOptStage
      )
    )
  }

  lazy val cgtaAutowire = {
    val AutowireVersion = "0.2.9"
    crossLib(
      jvm = libraryDependencies += "biz.cgta" %% "autowire" % AutowireVersion,
      sjs = libraryDependencies += "biz.cgta" %%%! "autowire" % AutowireVersion
    )
  }

  lazy val scalaAsync = crossLib(base = libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.1")

  case object Time extends CrossLib {

    case object MomentJs extends SjsLib {
      import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
      lazy val momentJsVersion = SettingKey[String]("moment-js-version")
      override lazy val settings = S(
        momentJsVersion := "2.7.0",
        libraryDependencies += "org.webjars" % "momentjs" % momentJsVersion.value,
        jsDependencies += "org.webjars" % "momentjs" % momentJsVersion.value / "moment.js"
      )
    }

    case object Joda extends JvmLib {
      lazy val jodaVersion = SettingKey[String]("joda-version")
      lazy val jodaConvertVersion = SettingKey[String]("joda-convert-version")
      override lazy val settings = S(
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


  //##########################################################################################
  //JVM ONLY DEPS!
  //##########################################################################################

  //Used by transformers / settings
  val jackson = jvmLib(L("org.codehaus.jackson" % "jackson-mapper-lgpl" % "1.4.3"))

  //Used by RichNode / CmePfConfigs
  val scalaXml = jvmLib(L("org.scala-lang.modules" %% "scala-xml" % "1.0.5"))

  //Used by Mel, which is used by order history tools
  val mongo = jvmLib(L("org.mongodb" % "mongo-java-driver" % "2.11.4"))

  //Used by active order store
  val h2 = jvmLib(L("com.h2database" % "h2" % "1.1.118"))

  //Used for a lot of stuff
  val amazonAws = jvmLib(L("com.amazonaws" % "aws-java-sdk" % "1.9.9"))

  //Variety of things
  val apacheCommons = jvmLib(L(
    //Used by CME FTP Downloader -> S3 Uploader (md5sums)
    "commons-codec" % "commons-codec" % "1.6",
    // PN Tailer (NUKE?)
    "commons-io" % "commons-io" % "2.4",
    //String escape utils used by inhouse db layer
    "commons-lang" % "commons-lang" % "2.4",
    //FTP Used by CME FTP Downloader
    "commons-net" % "commons-net" % "3.3"
  ))

  //Cme Mdp3
  val sbe = jvmLib(L("uk.co.real-logic" % "sbe" % "1.0.3-RC2"))

  //Thread Performance (todo)
  val javaThreadAffinity = jvmLib(L("net.openhft" % "affinity" % "2.2"))

  //##########################################################################################
  //SCALA JS DEPS!
  //##########################################################################################

  case object Dom extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy val domVersion = SettingKey[String]("dom-version")
    override lazy val settings = S(
      domVersion := "0.8.2",
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % domVersion.value
    )
  }

  case object JQuery extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy val jqueryVersion = SettingKey[String]("jquery-version")
    override lazy val settings = S(
      jqueryVersion := "0.8.1",
      libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % jqueryVersion.value
    )
  }


  case object ScalaJsReact extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    lazy val scalaJsReactVersion = SettingKey[String]("scala-js-react-version")
    override lazy val settings = S(
      scalaJsReactVersion := "0.9.2",
      libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion.value,
      libraryDependencies += "com.github.japgolly.scalajs-react" %%% "extra" % scalaJsReactVersion.value
    )
  }

  case object Scalatags extends SjsLib {
    import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
    val scalatagsVersion = SettingKey[String]("scalatags-version")
    override val settings = S(
      scalatagsVersion := "0.5.1",
      libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion.value
    )
  }


  //##########################################################################################
  //DEPS TO REMOVE
  //##########################################################################################

  //USED FOR API/RPC STANDALONE SERVER/CLIENT (AUTOWIRE BACKEND)
  case object Spray extends JvmLib {
    lazy val sprayVersion = SettingKey[String]("spray-version")
    override lazy val settings = S(
      sprayVersion := "1.3.2",
      libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion.value,
      libraryDependencies += "io.spray" %% "spray-client" % sprayVersion.value
    )
  }

  //USED BY SPRAY
  case object Akka extends JvmLib {
    lazy val akkaVersion = SettingKey[String]("akka-version")
    lazy val akkaReactiveExperimentalVersion = SettingKey[String]("akka-reactive-experimental-version")
    override lazy val settings: Seq[Setting[_]] = S(
      //      akkaVersion := "2.2.4",
      akkaVersion := "2.3.6",
      akkaReactiveExperimentalVersion := "0.9",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion.value,
      libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVersion.value,
      libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaVersion.value
    )
  }


  //Used by LogS3MoverMain
  case object SbtIO extends JvmLib {
    lazy val sbtVersion = SettingKey[String]("sbt-version")
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


  //##########################################################################################
  //FORMER DEPS (Kept for reference)
  //##########################################################################################

  //  val paranamer         = List("com.thoughtworks.paranamer" % "paranamer" % "2.0")


  //    val chronicle = List("net.openhft" % "chronicle" % "2.0.3")
  //    val mongoCasbah = List("org.mongodb" %% "casbah" % "2.7.3" exclude("org.slf4j", "slf4j-api"))

  //  //Replaced with libs folder version
  //  val javolution = List("org.javolution" % "javolution-core-java" % "6.1.0")

  //Replaced with libs folder version
  //    val quickfixj = List("quickfixj" % "quickfixj-all" % "1.4.0-CGTA")
  //    val hotspotfx = List("com.hotspotfx" % "gatewayapi" % "4.9.4")
  //    val onix = List("biz.onixs" % "fix-engine" % "1.10.5")


  //      ,
  //      libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaReactiveExperimentalVersion.value,
  //      libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaReactiveExperimentalVersion.value


  //  case object Mysql extends JvmLib {
  //    lazy val mysqlVersion = SettingKey[String]("mysql-version")
  //    override lazy val settings = S(
  //      mysqlVersion := "5.1.34",
  //      libraryDependencies += "mysql" % "mysql-connector-java" % mysqlVersion.value
  //    )
  //  }
  //
  //  case object Postgres extends JvmLib {
  //    override lazy val settings = S(
  //      libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"
  //    )
  //  }
  //
  //  case object Slick extends JvmLib {
  //    override lazy val settings = S(
  //      libraryDependencies += "com.typesafe.slick" %% "slick" % "2.1.0" exclude("org.slf4j", "slf4j-api")
  //    )
  //  }


  //Used with macros
  //  case object Reflect extends JvmLib {
  //    override lazy val settings = S(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)
  //  }


  //Used by hlj.tar.gz help (NUKE?)
  //libraryDependencies += "org.apache.commons" % "commons-compress" % "1.9"

  //Used by quickfix
  //libraryDependencies += "org.apache.mina" % "mina-core" % "1.1.0"


  //Was used by PN's File Tailer Uploader
  //  case object Jedis extends JvmLib {
  //    lazy val jedisVersion = SettingKey[String]("jedis-version")
  //    override lazy val settings = S(
  //      jedisVersion := "2.6.0",
  //      libraryDependencies += "redis.clients" % "jedis" % jedisVersion.value
  //    )
  //  }


  //  case object ScalaSsh extends JvmLib {
  //    lazy          val scalaSshVersion           = SettingKey[String]("scala-ssh-version")
  //    override lazy val settings: Seq[Setting[_]] = Seq[Setting[_]](
  //      scalaSshVersion := "0.7.0",
  //      libraryDependencies += "com.decodified" %% "scala-ssh" % "0.7.0",
  //      libraryDependencies += "org.bouncycastle" % "bcprov-jdk16" % "1.46",
  //      libraryDependencies += "com.jcraft" % "jzlib" % "1.1.3"
  //    )
  //  }


  //  case object Transfix extends JvmLib {
  //    override lazy val settings     = S(
  //      libraryDependencies += "net.openhft" % "transfix" %  "1.0.2-alpha"
  //    )
  //  }


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
