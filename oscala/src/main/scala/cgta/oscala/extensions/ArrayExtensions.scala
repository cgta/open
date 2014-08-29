package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/11/14 2:59 AM
//////////////////////////////////////////////////////////////

class ArrayExtensions[A](val xs: Array[A]) extends AnyVal {
  def getOpt(i : Int) : Option[A] = if (i >= 0 && i < xs.length) Some(xs(i)) else None
}