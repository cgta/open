package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/8/15 5:13 PM
//////////////////////////////////////////////////////////////

class OptionExtensions[A](val a : Option[A]) extends AnyVal {
  @inline def mapOrElse[B](ifEmpty: => B)(f: A => B): B = if (a.isEmpty) ifEmpty else f(a.get)
  @inline def &&[B](b : Option[B]) : Option[(A, B)] = if (a.isDefined && b.isDefined) Some(a.get -> b.get) else None
}