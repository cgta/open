package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 1/14/15 7:19 PM
//////////////////////////////////////////////////////////////

object TestIteratorExtensions extends FunSuite {

  test("nextOption") {
    Assert.isEquals(Option.empty, Iterator.empty.nextOption())
    val itr = Seq(1, 2, 3).iterator
    Assert.isEquals(Some(1), itr.nextOption())
    Assert.isEquals(Some(2), itr.nextOption())
    Assert.isEquals(Some(3), itr.nextOption())
    Assert.isEquals(None, itr.nextOption())

  }

  test("headOption") {
    val itr = List(1, 2).iterator
    Assert.isEquals(Some(1), itr.headOption())
    Assert.isEquals(Some(2), itr.headOption())
    Assert.isEquals(None, itr.headOption())
    Assert.isEquals(None, itr.headOption())
    Assert.isEquals(None, itr.headOption())
  }

  test("lastOption") {
    val itr = List(1, 2).iterator
    Assert.isEquals(Some(2), itr.lastOption())
    Assert.isEquals(None, itr.lastOption())
    Assert.isEquals(None, itr.lastOption())
    Assert.isEquals(None, itr.lastOption())
  }

}