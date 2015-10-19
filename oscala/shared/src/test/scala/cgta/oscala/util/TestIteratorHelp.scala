package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/3/14 6:20 PM
//////////////////////////////////////////////////////////////

object TestIteratorHelp extends FunSuite {
  test("fromFunction") {
    var x = 3
    def fn() = {
      x -= 1
      val r = if (x < 0) None else Some(x)
      r
    }
    locally {
      val itr = IteratorHelp.fromFunction(fn)
      Assert.isEquals(true, itr.hasNext)
      Assert.isEquals(true, itr.hasNext)
      Assert.isEquals(2, itr.next())
      Assert.isEquals(1, itr.next())
      Assert.isEquals(true, itr.hasNext)
      Assert.isEquals(0, itr.next())
      Assert.isEquals(false, itr.hasNext)
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.isEquals(false, itr.hasNext)

    }

    locally {
      val itr = IteratorHelp.fromFunction(() => None)
      Assert.isEquals(false, itr.hasNext)
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.isEquals(false, itr.hasNext)
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.isEquals(false, itr.hasNext)
    }

    locally {
      val itr = IteratorHelp.fromFunction(() => None)
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.intercepts[NoSuchElementException](itr.next())
      Assert.isEquals(false, itr.hasNext)
    }

  }

}