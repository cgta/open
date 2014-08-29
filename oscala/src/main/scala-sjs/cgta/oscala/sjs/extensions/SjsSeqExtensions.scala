package cgta.oscala
package sjs.extensions

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 10:45 PM
//////////////////////////////////////////////////////////////


class SjsSeqExtensions[A](val xs: Seq[A]) extends AnyVal {

  def toJsArray: js.Array[A] = {
    val ys = new js.Array[A](xs.size)
    var i = 0
    while (i < xs.length) {
      ys(i) = xs(i)
      i += 1
    }
    ys
  }

}