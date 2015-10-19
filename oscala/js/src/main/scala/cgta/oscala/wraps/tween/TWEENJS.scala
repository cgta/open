package cgta.oscala
package wraps.tween

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 4/28/14 5:23 PM
//////////////////////////////////////////////////////////////

@JSName("TWEEN")
@js.native
object TWEENJS extends js.Object {
  // Type definitions for tween.js r12
  // Project: https://github.com/sole/tween.js/
  // Definitions by: sunetos <https://github.com/sunetos>, jzarnikov <https://github.com/jzarnikov>
  // Definitions: https://github.com/borisyankov/DefinitelyTyped
  val REVISION: String = js.native
  def getAll(): js.Array[Tween] = js.native
  def removeAll(): Unit = js.native
  def add(tween: Tween): Unit = js.native
  def remove(tween: Tween): Unit = js.native
  def update(time: Double = js.native): Boolean = js.native

  val Easing       : Tween.Easing        = js.native
  val Interpolation: Tween.Interpolation = js.native
}

@JSName("TWEEN.Tween")
@js.native
class Tween(obj: js.Any = js.native) extends js.Object {
  def to(properties: js.Any, duration: Double): Tween = js.native
  def start(time: Double = js.native): Tween = js.native
  def stop(): Tween = js.native
  def delay(amount: Double): Tween = js.native
  def easing(easing: js.Function1[Double, Double]): Tween = js.native
  def interpolation(interpolation: js.Function2[js.Array[Double], Double, Double]): Tween = js.native
  def chain(tweens: Tween*): Tween = js.native
  def onStart(callback: js.Function1[js.Any, Unit]): Tween = js.native
  def onUpdate(callback: js.Function1[js.Any, Unit]): Tween = js.native
  def onComplete(callback: js.Function1[js.Any, Unit]): Tween = js.native
  def onStart(callback: js.Function0[Unit]): Tween = js.native
  def onUpdate(callback: js.Function0[Unit]): Tween = js.native
  def onComplete(callback: js.Function0[Unit]): Tween = js.native
  def update(time: Double): Boolean = js.native
  def repeat(times: Double): Tween = js.native
  def yoyo(enable: Boolean): Tween = js.native
}

object Tween {
  @js.native
  trait InOutEasingFns extends js.Object {
    def In(x: Double): Double = js.native
    def Out(x: Double): Double = js.native
    def InOut(x: Double): Double = js.native
  }

  @js.native
  trait LinearEasingFns extends js.Object {
    def None(x: Double): Double = js.native
  }


  @js.native
  trait Easing extends js.Object {
    val Linear     : LinearEasingFns = js.native
    val Quadratic  : InOutEasingFns  = js.native
    val Cubic      : InOutEasingFns  = js.native
    val Quartic    : InOutEasingFns  = js.native
    val Quintic    : InOutEasingFns  = js.native
    val Sinusoidal : InOutEasingFns  = js.native
    val Exponential: InOutEasingFns  = js.native
    val Circular   : InOutEasingFns  = js.native
    val Elastic    : InOutEasingFns  = js.native
    val Back       : InOutEasingFns  = js.native
    val Bounce     : InOutEasingFns  = js.native
  }

  @js.native
  trait InterpolationUtils extends js.Object {
    def Linear(p0: Double, p1: Double, t: Double): Double = js.native
    def Bernstein(n: Double, i: Double): Double = js.native
    def Factorial(n: Double): Double = js.native
  }

  @js.native
  trait Interpolation extends js.Object {
    def Linear(v: js.Array[Double], k: Double): Double = js.native
    def Bezier(v: js.Array[Double], k: Double): Double = js.native
    def CatmullRom(v: js.Array[Double], k: Double): Double = js.native
    val Utils: InterpolationUtils = js.native
  }
}

