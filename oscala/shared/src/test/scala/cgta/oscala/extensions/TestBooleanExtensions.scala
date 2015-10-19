package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/11/14 3:30 AM
//////////////////////////////////////////////////////////////


object TestBooleanExtensions extends FunSuite {

  test("ifElse") {
    Assert.isEquals('t, true.ifElse('t, 'f))
    Assert.isEquals('t, true.ifElse('t, sys.error("bottom")))
    Assert.isEquals('f, false.ifElse('t, 'f))
    Assert.isEquals('f, false.ifElse(sys.error("bottom"), 'f))
  }

}