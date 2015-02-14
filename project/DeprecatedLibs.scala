import sbt._
import sbt.Keys._

import sbt.CrossVersion

//A list of libraries
object OldLibs {


  val slf4jForTests = List(
    "org.slf4j" % "slf4j-api" % "1.5.6" % "test",
    "org.slf4j" % "slf4j-nop" % "1.5.6" % "test")

  val async        = List("org.scala-lang.modules" %% "scala-async" % "0.9.1")

  val chronicle = List("net.openhft" % "chronicle" % "2.0.3")
  val openFast  = List("org.openfast" % "openfast" % "1.1.1")

  val javolution = List("org.javolution" % "javolution-cgta-java" % "6.1.0")
  val slick      = List("com.typesafe.slick" %% "slick" % "2.1.0" exclude("org.slf4j", "slf4j-api"))



  val h2         = List("com.h2database" % "h2" % "1.1.118")
  val postgresql = List("postgresql" % "postgresql" % "9.1-901.jdbc4")
  val slickAll   = slick ++ h2 ++ postgresql

  val mongo       = List("org.mongodb" % "mongo-java-driver" % "2.11.4")
  val mongoCasbah = List("org.mongodb" %% "casbah" % "2.7.3" exclude("org.slf4j", "slf4j-api"))

  val wekaAll = Seq(
    "nz.ac.waikato.cms.weka" % "weka-dev" % "3.7.11",
    "nz.ac.waikato.cms.weka" % "RBFNetwork" % "1.0.7"
  )


  object CdpOnly {
    val jackson           = List("org.codehaus.jackson" % "jackson-mapper-lgpl" % "1.4.3")
    val paranamer         = List("com.thoughtworks.paranamer" % "paranamer" % "2.0")

    val slf4jApi = List("org.slf4j" % "slf4j-api" % "1.6.6")
    val jetty    = List("org.eclipse.jetty.aggregate" % "jetty-all" % "8.0.1.v20110908")

    val disruptor = List("com.lmax" % "disruptor" % "3.1.1")

    val quickfixj = List("quickfixj" % "quickfixj-all" % "1.4.0-CGTA")
    val hotspotfx = List("com.hotspotfx" % "gatewayapi" % "4.9.4")
    val onix = List("biz.onixs" % "fix-engine" % "1.10.5")

  }

}
