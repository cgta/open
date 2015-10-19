package cgta.oscala
package extensions

import scala.collection.mutable.ArrayBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/4/15 1:30 PM
//////////////////////////////////////////////////////////////

class ArrayBufferExtensions[A](val xs : ArrayBuffer[A]) extends AnyVal {
  def setSparse(i : Int, a : A, hole : => A) : Unit = {
    require(i >= 0, s"$i<0")
    if (i < xs.size) {
      xs(i) = a
    } else if(i == xs.size) {
      xs += a
    } else {
      //grow this until we are able to append onto the end
      while(xs.size < i) {
        xs += hole
      }
      xs += a
    }
  }
}