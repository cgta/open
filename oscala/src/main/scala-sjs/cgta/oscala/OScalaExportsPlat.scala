package cgta.oscala

import cgta.oscala.sjs.extensions.{SjsSeqExtensions, SjsArrayExtensions, SjsAnyExtensions, JsArrayExtensions, JsAnyExtensions}
import cgta.oscala.sjs.lang.JsConsole


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/28/14 3:50 PM
//////////////////////////////////////////////////////////////

import scala.language.dynamics
import scala.scalajs.js



class JsSetAll[A <: js.Any](val x: A) extends scala.Dynamic {
  def applyDynamicNamed(name: java.lang.String)(fields: (java.lang.String, Any)*) : A = {
    if (name != "apply") {
      sys.error("only apply method is supported")
    }
    val y = x.asInstanceOf[js.Dictionary[Any]]
    fields.foreach { case (name, value) =>
      y(name) = value
    }
    x
  }
}

trait OScalaExportsPlat {

  val global    = js.Dynamic.global
  val console   = global.console.asInstanceOf[JsConsole]
  val JSON      = global.JSON
  val undefined = global.undefined
  val window    = global.window
  val document  = org.scalajs.dom.document


  def newObject = js.Object().asInstanceOf[js.Dynamic]
  def newJs(clazz: js.Dynamic)(args: js.Any*): js.Dynamic = js.Dynamic.newInstance(clazz)(args: _*)
  val OBJ = js.Dynamic.literal

  def jfor[A](init: A, p: A => Boolean, inc: A => A)(f: A => Unit) {
    var x = init
    while (p(x)) {
      f(x)
      x = inc(x)
    }
  }

  def setAll[A <: js.Any](x: A): JsSetAll[A] = new JsSetAll[A](x)

  implicit def jsAnyExtensions(a: js.Any) = new JsAnyExtensions(a)
  implicit def jsArrayExtensions[A](a: js.Array[A]) = new JsArrayExtensions[A](a)
  implicit def sjsAnyExtensions(a: Any) = new SjsAnyExtensions(a)
  implicit def sjsArrayExtensions[A](a: Array[A]) = new SjsArrayExtensions[A](a)
  implicit def sjsSeqExtensions[A](a: Seq[A]) = new SjsSeqExtensions[A](a)

  //  val * = js.Dynamic.literal
  //  type * = js.Dynamic
  //  type ** = js.Object with js.Dynamic
  //  def log(args: js.Any*) = console.log(args: _*)



  //  implicit object QueueExecutionContext extends ExecutionContext {
  //
  //    def execute(runnable: Runnable) = {
  //      val lambda: js.Function = () =>
  //        try {runnable.run()} catch {case t: Throwable => reportFailure(t)}
  //      js.Dynamic.global.setTimeout(lambda, 0)
  //    }
  //
  //    def reportFailure(t: Throwable) =
  //      Console.err.println("Failure in async execution: " + t)
  //
  //  }


}