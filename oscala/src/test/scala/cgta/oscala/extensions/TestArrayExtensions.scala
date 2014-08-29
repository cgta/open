package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/11/14 3:29 AM
//////////////////////////////////////////////////////////////

object TestArrayExtensions extends FunSuite {

  test("getOpt") {
    val xs = Array(0,1,2,3)
    Assert.isEquals(None, xs.getOpt(-1))
    Assert.isEquals(None, xs.getOpt(5))
    Assert.isEquals(Some(0), xs.getOpt(0))
    Assert.isEquals(Some(1), xs.getOpt(1))
  }

}