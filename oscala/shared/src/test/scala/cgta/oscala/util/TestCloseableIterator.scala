package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/7/15 12:02 PM
//////////////////////////////////////////////////////////////

object TestCloseableIterator extends FunSuite {

  class TestIterator(val xs: List[Int]) extends CloseableIterator[Int] {
    var closeCnt: Int = 0
    val itr = xs.iterator
    override def close(): Unit = closeCnt += 1
    override def next(): Int = itr.next()
    override def hasNext: Boolean = itr.hasNext
  }
  private def i(xs: Int*): TestIterator = new TestIterator(xs.toList)

  test("Multiple Closes") {
    val itrs = List(i(1, 2, 3), i(4, 5, 6), i(), i())
    val itr = CloseableIterator(itrs)

    Closing(itr) { itr =>
      Assert.isEquals(itrs.flatMap(_.xs), itr.map(x => x).toList)
    }
    Assert.isEquals(List.fill(itrs.size)(1), itrs.map(_.closeCnt))
  }

}