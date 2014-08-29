package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/15/14 11:12 PM
//////////////////////////////////////////////////////////////

class DoubleExtensions(val d: Double) extends AnyVal {
  def divideByAtLeastOne(denom: Double) = d / math.max(denom, 1d)
}