import sbt._
import sbt.Keys._

import play.Project
import sbt.CrossVersion

//A list of libraries
object Libs {
//  val slf4j = List(
//    "org.slf4j" % "slf4j-api" % "1.5.6",
//    "org.slf4j" % "slf4j-nop" % "1.5.6")

  val async        = List("org.scala-lang.modules" %% "scala-async" % "0.9.1")
  val macrosQuasi  = List("org.scalamacros" %% "quasiquotes" % "2.0.0")
  val macrosPlugin = "org.scalamacros" %% "paradise" % "2.0.0" cross CrossVersion.full

  val chronicle = List("net.openhft" % "chronicle" % "2.0.3")
  val openFast  = List("org.openfast" % "openfast" % "1.1.1")
  val jodaTime  = List(
    "joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.2"
  )

  val javolution = List("org.javolution" % "javolution-cgta-java" % "6.1.0")
  val slick = List("com.typesafe.slick" %% "slick" % "2.1.0" exclude("org.slf4j", "slf4j-api"))

  val apacheCommons = List("commons-io" % "commons-io" % "2.4")

  val h2         = List("com.h2database" % "h2" % "1.1.118")
  val postgresql = List("postgresql" % "postgresql" % "9.1-901.jdbc4")
  val slickAll   = slick ++ h2 ++ postgresql

  val mongo       = List("org.mongodb" % "mongo-java-driver" % "2.11.4")
  val mongoCasbah = List("org.mongodb" %% "casbah" % "2.6.5" exclude("org.slf4j", "slf4j-api"))

  val weka = Seq("nz.ac.waikato.cms.weka" % "weka-dev" % "3.7.11")

}
