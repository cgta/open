package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/31/14 4:22 PM
//////////////////////////////////////////////////////////////

object TestIntExtensions extends FunSuite {

  test("Times") {
    var x = 0
    10 times (x += 1)
    Assert.isEquals(10,x)
  }

}