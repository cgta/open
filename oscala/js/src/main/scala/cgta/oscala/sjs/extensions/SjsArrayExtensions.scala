package cgta.oscala
package sjs.extensions

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/21/14 6:01 PM
//////////////////////////////////////////////////////////////


class SjsArrayExtensions[A](val xs: Array[A]) extends AnyVal {

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