package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/14/14 8:19 PM
//////////////////////////////////////////////////////////////

object IteratorExtensionsTest extends FunSuite {

  test("nextOption") {
    val itr = List(1).iterator
    Assert.isEquals(Some(1), itr.nextOption())
    Assert.isEquals(None, itr.nextOption())
    Assert.isEquals(None, itr.nextOption())
    Assert.isEquals(None, itr.nextOption())
  }

}