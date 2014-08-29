package cgta.oscala
package extensions

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by kklipsch @ 11/27/13 10:20 AM
//////////////////////////////////////////////////////////////


class Func1Extensions[A, B](val func: Function[A, B]) {
  def toPartial: PartialFunction[A, B] = { case x => func(x) }
  def tupled: Tuple1[A] => B = { case Tuple1(a) => func(a) }
}