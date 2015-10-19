package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/18/15 6:02 PM
//////////////////////////////////////////////////////////////

object TestOCache extends FunSuite {

  test("OCache functions properly") {
    val cache = OCache.empty[Int, Int](100)

    Assert.isEquals(None, cache.get(5))
    Assert.isEquals(Some(5), cache.getOrSet(5, Some(5)))
    Assert.isEquals(Some(5), cache.get(5))
  }

}