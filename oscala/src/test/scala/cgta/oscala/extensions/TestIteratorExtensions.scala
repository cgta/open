package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 1/14/15 7:19 PM
//////////////////////////////////////////////////////////////

object TestIteratorExtensions extends FunSuite {

  test("hasNextOption") {
    Assert.isEquals(Option.empty, Iterator.empty.nextOption())
    val itr = Seq(1,2,3).iterator
    Assert.isEquals(Some(1), itr.nextOption())
    Assert.isEquals(Some(2), itr.nextOption())
    Assert.isEquals(Some(3), itr.nextOption())
    Assert.isEquals(None, itr.nextOption())

  }

}