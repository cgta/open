package cgta.oscala
package extensions

import cgta.oscala.util.OFormat


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/31/14 4:22 PM
//////////////////////////////////////////////////////////////

class IntExtensions(val x : Int) extends AnyVal {

  def times(f  : => Unit) {
    var i = 0
    while (i < x) {
      f
      i += 1
    }
  }

  def toSiString(showDigits : Int = 4) = OFormat.si(x.toDouble, showDigits = showDigits)

}