package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 10/15/13 12:04 PM
//////////////////////////////////////////////////////////////


object TestIMapExtensions extends FunSuite {
  test("MapValue") {
    assert(IMap(1 -> 2, 3 -> 4) == IMap(1 -> 2, 3 -> 5).mapValue(3, _.map(x => 4)))
    assert(IMap(1 -> 2, 3 -> 4) == IMap(1 -> 2).mapValue(3, x => Some(4)))
    assert(IMap(1 -> 2) == IMap(1 -> 2, 3 -> 4).mapValue(3, x => None))
  }
}