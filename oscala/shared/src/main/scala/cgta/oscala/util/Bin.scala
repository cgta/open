package cgta.oscala
package util

import cgta.otest.CanAssertEq


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/23/13 5:08 PM
//////////////////////////////////////////////////////////////


trait BinL1 {
  implicit def binEqs[A, B, AA, BB](implicit
    e1: AA <:< Bin[A],
    e2: BB <:< Bin[B],
    e3: CanAssertEq[A, B]): CanAssertEq[AA, BB] = CanAssertEq.singleton.asInstanceOf[CanAssertEq[AA, BB]]

}

object Bin extends BinL1 {
  implicit object BinFailure extends Failure[Bin] {
    def create[A](e: Throwable): Bin[A] = BinNone(BinError(e))
    def create[A](t: Bin[A]): Option[Throwable] = t.error.map(_.throwable)

  }

  def attempt[A](f: => A): Bin[A] = {
    Failure.tryIt[Bin, A](BinSome(f))
  }

  trait IWithFilter[+A] {
    def flatMap[B](f: A => Bin[B]): Bin[B]
    def map[B](f: A => B): Bin[B]
    def foreach(f: A => Unit)
    def withFilter(f: (A) => Boolean): Bin.IWithFilter[A]
  }


  implicit val binNoneNothingEqs: CanAssertEq[BinNone, Bin[Nothing]] = CanAssertEq.singleton.asInstanceOf[CanAssertEq[BinNone, Bin[Nothing]]]
  implicit val binNothingNoneEqs: CanAssertEq[Bin[Nothing], BinNone] = CanAssertEq.singleton.asInstanceOf[CanAssertEq[Bin[Nothing], BinNone]]

}


/**
 * Like Lifts box class, but with no legacy costs.
 * @tparam A The type of the object that goes in the bin
 */
sealed trait Bin[+A] {

  def get: A
  final def getError: BinError = error.get
  def error: Option[BinError]
  def isEmpty: Boolean
  def setMsg(msg: String): Bin[A]
  final def nonEmpty: Boolean = !isEmpty
  final def isDefined: Boolean = nonEmpty
  final def hasError: Boolean = error.isDefined

  final def getOrElse[B >: A](f: => B): B = if (isDefined) get else f
  final def orElse[B >: A](f: => Bin[B]): Bin[B] = if (isDefined) this else f

  final def toOption: Option[A] = if (isEmpty) None else Some(get)
  final def iterator: Iterator[A] = if (isEmpty) Iterator.empty else Iterator.single(get)

  def foreach(f: A => Unit)
  def map[B](f: A => B): Bin[B]
  def flatMap[B](f: A => Bin[B]): Bin[B] = map(f).flatten
  def flatten[B](implicit ev: A <:< Bin[B]): Bin[B]
  def withFilter(f: A => Boolean): Bin.IWithFilter[A]
  def filter(f: A => Boolean): Bin[A] = withFilter(f).map(x => x)

}


object BinSome {
  class WithFilter[A](b: BinSome[A], p: A => Boolean) extends Bin.IWithFilter[A] {
    def flatMap[B](f: (A) => Bin[B]): Bin[B] = if (p(b.x)) b.flatMap(f) else BinNone()
    def map[B](f: (A) => B): Bin[B] = if (p(b.x)) b.map(f) else BinNone()
    def foreach(f: (A) => Unit) { if (p(b.x)) b.foreach(f) }
    def withFilter(f: (A) => Boolean): Bin.IWithFilter[A] = new WithFilter[A](b, x => p(x) && f(x))
  }
}

/**
 *
 * @param x The value of the parameter in the bin
 * @tparam A The type of the object that goes in the bin
 */
final case class BinSome[+A](x: A) extends Bin[A] {
  override def isEmpty = false
  override def get = x
  override def error = None
  override def setMsg(msg: String): Bin[A] = this
  def foreach(f: A => Unit) { f(x) }
  def map[B](f: A => B): Bin[B] = BinSome(f(x))
  def flatten[B](implicit ev: A <:< Bin[B]): Bin[B] = ev(x)
  def withFilter(f: (A) => Boolean): Bin.IWithFilter[A] = {
    new BinSome.WithFilter(this, f)
  }

}


object BinNone {

  class WithFilter(n: Bin[Nothing]) extends Bin.IWithFilter[Nothing] {
    def flatMap[B](f: Nothing => Bin[B]): Bin[B] = n flatMap f
    def map[B](f: Nothing => B): Bin[B] = n map f
    def foreach(f: Nothing => Unit) { /*Do Nothing*/ }
    def withFilter(f: (Nothing) => Boolean): Bin.IWithFilter[Nothing] = this
  }


  val empty : BinNone = new BinNone(None)
  def apply(error: Option[BinError]): BinNone = if (error.isEmpty) empty else new BinNone(error)
  def apply(): BinNone = empty
  def apply(error: BinError): BinNone = new BinNone(Some(error))
  def apply(msg: String): BinNone = new BinNone(Some(BinError(msg, None, None)))
  def apply(exception: Throwable): BinNone = new BinNone(Some(BinError("", Some(exception), None)))
  def unapply(x: Bin[Nothing]): Option[Option[BinError]] = Some(x.error)
}

/**
 *
 * @param error An optional field that describes that error resulted in this bin being empty
 */
final class BinNone private(override val error: Option[BinError]) extends Bin[Nothing] {
  override def isEmpty = true
  override def get = throw new NoSuchElementException(s"BinNone.get $error")
  override def setMsg(msg: String): Bin[Nothing] = BinNone(error.map(_.copy(msg = msg)).getOrElse(BinError(msg)))
  def map[B](f: Nothing => B): Bin[B] = this
  def flatten[B](implicit ev: Nothing <:< Bin[B]): Bin[B] = this
  def foreach(f: (Nothing) => Unit) { /*Do Nothing*/ }
  def withFilter(f: (Nothing) => Boolean): Bin.IWithFilter[Nothing] = new BinNone.WithFilter(this).withFilter(f)
  override def equals(that: Any): Boolean = {
    that match {
      case that: Bin[_] => that.error == this.error
      case _ => false
    }
  }
}

object BinError {
  def apply(msg: String): BinError = BinError(msg, None, None)
  def apply(exception: Throwable): BinError = BinError("", Some(exception), None)
}
final case class BinError(msg: String, exception: Option[Throwable], chain: Option[BinError]) extends FailureLike