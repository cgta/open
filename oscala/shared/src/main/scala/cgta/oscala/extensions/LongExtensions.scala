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

class LongExtensions(val x : Long) extends AnyVal {
  def toSiString(showDigits : Int = 4) = OFormat.si(x.toDouble, showDigits = showDigits)
}