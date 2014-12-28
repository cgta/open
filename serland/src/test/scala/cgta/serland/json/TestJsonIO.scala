package cgta.serland
package json

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 12/16/14 11:21 AM
//////////////////////////////////////////////////////////////

object TestJsonIO extends FunSuite {

  test("numbers are unchanged") {
    Assert.isEquals("500",JsonIO.writeCompact(JsonIO.read("500")))
  }

}