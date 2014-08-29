package cgta.oscala
package extensions

import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:54 PM
//////////////////////////////////////////////////////////////


object TestByteExtensions extends FunSuite {

  test("toUInt32") {
    Assert.isEquals(255, (-1).toByte.toUInt32)
  }

}