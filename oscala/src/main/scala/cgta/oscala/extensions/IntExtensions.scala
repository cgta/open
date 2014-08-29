package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
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


}