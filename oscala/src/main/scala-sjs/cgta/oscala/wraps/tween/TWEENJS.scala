package cgta.oscala
package wraps.tween

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/28/14 5:23 PM
//////////////////////////////////////////////////////////////

@JSName("TWEEN")
object TWEENJS extends js.Object {
  // Type definitions for tween.js r12
  // Project: https://github.com/sole/tween.js/
  // Definitions by: sunetos <https://github.com/sunetos>, jzarnikov <https://github.com/jzarnikov>
  // Definitions: https://github.com/borisyankov/DefinitelyTyped
  val REVISION: js.String = ???
  def getAll(): js.Array[Tween] = ???
  def removeAll(): Unit = ???
  def add(tween: Tween): Unit = ???
  def remove(tween: Tween): Unit = ???
  def update(time: js.Number = ???): js.Boolean = ???

  val Easing       : Tween.Easing        = ???
  val Interpolation: Tween.Interpolation = ???
}

@JSName("TWEEN.Tween")
class Tween(obj: js.Any = ???) extends js.Object {
  def to(properties: js.Any, duration: js.Number): Tween = ???
  def start(time: js.Number = ???): Tween = ???
  def stop(): Tween = ???
  def delay(amount: js.Number): Tween = ???
  def easing(easing: js.Function1[js.Number, js.Number]): Tween = ???
  def interpolation(interpolation: js.Function2[js.Array[js.Number], js.Number, js.Number]): Tween = ???
  def chain(tweens: Tween*): Tween = ???
  def onStart(callback: js.Function1[js.Any, Unit]): Tween = ???
  def onUpdate(callback: js.Function1[js.Any, Unit]): Tween = ???
  def onComplete(callback: js.Function1[js.Any, Unit]): Tween = ???
  def onStart(callback: js.Function0[Unit]): Tween = ???
  def onUpdate(callback: js.Function0[Unit]): Tween = ???
  def onComplete(callback: js.Function0[Unit]): Tween = ???
  def update(time: js.Number): js.Boolean = ???
  def repeat(times: js.Number): Tween = ???
  def yoyo(enable: js.Boolean): Tween = ???
}

object Tween {
  trait InOutEasingFns extends js.Object {
    def In(x: js.Number): js.Number = ???
    def Out(x: js.Number): js.Number = ???
    def InOut(x: js.Number): js.Number = ???
  }

  trait LinearEasingFns extends js.Object {
    def None(x: js.Number): js.Number
  }


  trait Easing extends js.Object {
    val Linear     : LinearEasingFns = ???
    val Quadratic  : InOutEasingFns  = ???
    val Cubic      : InOutEasingFns  = ???
    val Quartic    : InOutEasingFns  = ???
    val Quintic    : InOutEasingFns  = ???
    val Sinusoidal : InOutEasingFns  = ???
    val Exponential: InOutEasingFns  = ???
    val Circular   : InOutEasingFns  = ???
    val Elastic    : InOutEasingFns  = ???
    val Back       : InOutEasingFns  = ???
    val Bounce     : InOutEasingFns  = ???
  }

  trait InterpolationUtils extends js.Object {
    def Linear(p0: js.Number, p1: js.Number, t: js.Number): js.Number = ???
    def Bernstein(n: js.Number, i: js.Number): js.Number = ???
    def Factorial(n: js.Number): js.Number = ???
  }

  trait Interpolation extends js.Object {
    def Linear(v: js.Array[js.Number], k: js.Number): js.Number = ???
    def Bezier(v: js.Array[js.Number], k: js.Number): js.Number = ???
    def CatmullRom(v: js.Array[js.Number], k: js.Number): js.Number = ???
    val Utils: InterpolationUtils = ???
  }
}

