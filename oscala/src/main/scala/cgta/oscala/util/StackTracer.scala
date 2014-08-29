package cgta.oscala
package util

import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import scala.util.control.NonFatal


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/17/14 11:24 AM
//////////////////////////////////////////////////////////////

object StackTracer {

  def trace(e: Throwable): List[String] = {
    val lb = new ListBuffer[Either[String, StackTraceElement]]
    @tailrec
    def loop(e: Throwable) {
      val msg = try {e.getMessage} catch {case NonFatal(_) => "UNABLE TO DISPLAY EXCEPTION MESSAGE"}
      val name = e.getClass.getName
      lb += Left(name + ": " + msg)
      lb ++= e.getStackTrace.map(Right(_))
      val cause = e.getCause
      if (cause != null) loop(cause)
    }
    loop(e)
    trace(lb.toList)
  }

  def trace(t: Seq[Either[String, StackTraceElement]]): List[String] = {
    val lb = new ListBuffer[String]
    var first = true
    t.foreach {
      case Left(msg) =>
        lb += (if (first) "Exception " else "Caused by: ") + msg
        first = false
      case Right(ste) =>
        lb += "  at " + ste.toString
    }
    lb.toList
  }

}