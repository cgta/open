package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 1/14/15 6:38 PM
//////////////////////////////////////////////////////////////

object TestTraversableOnceExtensions extends FunSuite {
  def months = Seq(1 -> "Jan", 2 -> "Feb", 3 -> "Mar", 4 -> "Apr", 5 -> "May")

  test("MinOpt") {
    val xs = Iterable.empty[Int]
    val ys = Seq(2, 5, 7, 40, -32)
    Assert.isEquals(None, xs.minOpt)
    Assert.isEquals(Some(-32), ys.minOpt)

  }

  test("MaxOpt") {
    val xs = Seq.empty[Int]
    val ys = Seq(2, 5, Int.MaxValue, 7, 40)

    Assert.isEquals(None, xs.maxOpt)
    Assert.isEquals(Some(Int.MaxValue), ys.maxOpt)

  }

  test("MinByOpt") {
    val xs = months
    Assert.isEquals(Some(4 -> "Apr"), xs.minByOpt(x => x._2))
    Assert.isEquals(Some(1 -> "Jan"), xs.minByOpt(x => x._1))
  }

  test("MaxByOpt") {
    val xs = months
    Assert.isEquals(Some(5 -> "May"), xs.maxByOpt(x => x._2))
    Assert.isEquals(Some(5 -> "May"), xs.maxByOpt(x => x._1))
  }

}