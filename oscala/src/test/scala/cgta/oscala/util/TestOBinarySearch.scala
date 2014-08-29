package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/18/14 4:40 PM
//////////////////////////////////////////////////////////////

object TestOBinarySearch extends FunSuite {

  val SampleCount = 1000

  test("BinarySearch") {
    import OBinarySearch.bs
    val xs = IVec(3, 5, 7, 9)
    def f(x: Int) = bs(xs, x)
    Assert.isEquals(0, f(3))
    Assert.isEquals(1, f(5))
    Assert.isEquals(2, f(7))
    Assert.isEquals(3, f(9))
    Assert.isEquals(-1, f(-1))
    Assert.isEquals(-2, f(4))
    Assert.isEquals(-3, f(6))
    Assert.isEquals(-4, f(8))
    Assert.isEquals(-5, f(11))
  }

  def repeaty = IVec(3, 3, 3, 3, 5, 5, 5, 5, 5, 5, 7, 7, 7, 7, 9, 9, 9, 9, 9, 9)

  test("BinarySearchRepeats") {
    import OBinarySearch.bs
    val xs = repeaty
    def at(x: Int) = xs indexOf x
    def before(x: Int) = -(at(x) + 1)
    def afterEnd = -(xs.size + 1)
    def f(x: Int) = bs(xs, x)
    Assert.isEquals(at(3), f(3))
    Assert.isEquals(at(5), f(5))
    Assert.isEquals(at(7), f(7))
    Assert.isEquals(at(9), f(9))
    Assert.isEquals(before(3), f(-1))
    Assert.isEquals(before(5), f(4))
    Assert.isEquals(before(7), f(6))
    Assert.isEquals(before(9), f(8))
    Assert.isEquals(afterEnd, f(11))
  }


  test("Insert") {

    locally {
      val xs = repeaty.toList
      def doInsert(x: Int, expected: List[Int]) {
        val ys = OBinarySearch.insert(xs.toArray, x)
        Assert.isEquals(expected, ys.toList)
      }
      doInsert(0, 0 :: repeaty.toList)
      doInsert(3, (3 :: repeaty.toList).sorted)
      doInsert(4, (4 :: repeaty.toList).sorted)
      doInsert(7, (7 :: repeaty.toList).sorted)
      doInsert(9, (9 :: repeaty.toList).sorted)
      doInsert(10, (10 :: repeaty.toList).sorted)
    }


  }

}