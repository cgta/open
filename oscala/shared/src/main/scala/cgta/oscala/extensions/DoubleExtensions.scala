package cgta.oscala
package extensions

import cgta.oscala.util.OFormat


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/15/14 11:12 PM
//////////////////////////////////////////////////////////////

class DoubleExtensions(val d: Double) extends AnyVal {
  def divideByAtLeastOne(denom: Double) = d / math.max(denom, 1d)

  def toSiString(showDigits : Int = 4) = OFormat.si(d, showDigits = showDigits)
}