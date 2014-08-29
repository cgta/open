package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/11/14 3:27 AM
//////////////////////////////////////////////////////////////

class BooleanExtensions(val b: Boolean) extends AnyVal {
  @inline def ifElse[A](t: => A, f: => A): A = if (b) t else f
  @inline def trueToSome[A](t: => A): Option[A] = if (b) Some(t) else None
  @inline def falseToSome[A](f: => A): Option[A] = if (!b) Some(f) else None

}