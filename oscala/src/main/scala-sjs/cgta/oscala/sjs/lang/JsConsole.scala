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
  def trace(): Unit = js.native
  def time(s: String): Unit = js.native
  def timeEnd(s: String): Unit = js.native
  def timeStamp(s: String): Unit = js.native
  def debug(xs: Any*): Unit = js.native
  def log(xs: Any*): Unit = js.native
  def warn(xs: Any*): Unit = js.native
  def error(xs: Any*): Unit = js.native
}