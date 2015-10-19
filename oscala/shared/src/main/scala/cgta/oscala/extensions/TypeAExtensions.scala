package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:50 PM
//////////////////////////////////////////////////////////////


class TypeAExtensions[A](val a: A) extends AnyVal {
  def oIfIs(f: PartialFunction[A, Unit]): A = {
    if (f.isDefinedAt(a)) f(a)
    a
  }

  def oIf[B](p: A => Boolean, t: A => B, f: A => B): B = {
    if (p(a)) t(a) else f(a)
  }

  //use this value to execute a side effect
  def oEff(f: A => Unit): A = { f(a); a }
  //execute a side effect, ignoring this value
  def oEff0(f: => Unit): A = { f; a }
  //map this value into a new one
  def oMap[B](f: A => B): B = f(a)
  //replace this value with another one
  def oReplace[B](b: => B): B = b

  def toSome : Option[A] = Some(a)
  def nullSafe: Option[A] = if (a == null) None else Some(a)

  /**
   * Type safe equality
   */
  def =?=[B](that: B)(implicit ev: B =:= A): Boolean = a == that

  /**
   * Type safe equality
   */
  def !=?=[B](that: B)(implicit ev: B =:= A): Boolean = a != that



}