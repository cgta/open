package cgta.oscala
package sjs.lang

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/19/14 11:18 PM
//////////////////////////////////////////////////////////////

object JsConsole {
  implicit class RichConsole(val c: JsConsole) extends AnyVal {
    def timed[A](s: String)(blk: => A): A = {
      c.time(s)
      val r = try {
        blk
      } finally {
        c.timeEnd(s)
      }
      r
    }
  }
}

trait JsConsole extends js.Object {
  def trace()
  def time(s: String)
  def timeEnd(s: String)
  def timeStamp(s: String)
  def debug(xs: Any*)
  def log(xs: Any*)
  def warn(xs: Any*)
  def error(xs: Any*)
}