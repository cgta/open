package cgta.oscala
package util.debugging


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/28/14 9:09 PM
//////////////////////////////////////////////////////////////

trait PrintPlat extends PRINT {

//  val timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")
//  def timeInfo() : String = timeFormatter.print(new DateTime())

  private def lineInfo(): String = getLineInfo.getOrElse("???")

  private val ignoredElements = IVec(
    "cgta.oscala.util.debugging",
    "java.lang.Thread.getStackTrace"
  ).map(_.toUpperCase)

  private def ignoreStackTraceElement(stackTraceElement: String): Boolean = {
    val s = stackTraceElement.toUpperCase
    ignoredElements.exists(x => s startsWith x)
  }

  private def getLineInfo: Option[String] = {
    Thread.currentThread.getStackTrace.iterator.map(_.toString).dropWhile(ignoreStackTraceElement).nextOption match {
      case None => None
      case Some(s) =>
        val Regex = """.*\((.*)\).*""".r
        s match {
          case Regex(line) => Some(line)
          case _ => None
        }
    }
  }


  override final def |(msg: Any) {
//    println(s"${timeInfo()} [${lineInfo()}] -- $x")
    println(s"[${lineInfo()}] -- $msg")

  }
}