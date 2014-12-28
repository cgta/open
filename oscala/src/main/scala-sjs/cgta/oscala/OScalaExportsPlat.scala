package cgta.oscala

import cgta.oscala.sjs.extensions.{JsAnyExtensions, JsArrayExtensions, SjsAnyExtensions, SjsArrayExtensions, SjsSeqExtensions}

import scala.concurrent.ExecutionContext
import scala.language.dynamics
import scala.scalajs.js


class JsSetAll[A <: js.Any](val x: A) extends scala.Dynamic {
  def applyDynamicNamed(name: java.lang.String)(fields: (java.lang.String, Any)*): A = {
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

trait OScalaExportsPlat extends OScalaExportsShared {

  override val defaultExecutionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.queue

  //  val console   = org.scalajs.dom.window.console
  //Results in


  def global   = js.Dynamic.global
  def console  = js.Dynamic.global.console
  def JSON     = js.JSON
  def undefined = js.undefined
  def window   = org.scalajs.dom.window
  def document = org.scalajs.dom.document

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

  //Functions can be used for higher arities
  implicit def jsF0isF1[A, R](f: js.Function0[R]): js.Function1[Any, R] = f.asInstanceOf[js.Function1[Any, R]]
  implicit def f0isJsF1[A, R](f: () => R): js.Function1[Any, R] = jsF0isF1(f: js.Function0[R])


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