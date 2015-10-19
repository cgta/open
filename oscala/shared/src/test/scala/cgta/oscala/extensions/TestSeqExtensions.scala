package cgta.oscala
package extensions

import cgta.otest.FunSuite
//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/18/13 6:37 PM
//////////////////////////////////////////////////////////////


object TestSeqExtensions extends FunSuite {
  def months = Seq(1 -> "Jan", 2 -> "Feb", 3 -> "Mar", 4 -> "Apr", 5 -> "May")

  test("RemoveDuplicatesBy") {
    //Go over this list and use the fst tuple value
    //for checking for duplication, choose the highest
    //second tuple value to break ties
    val xs = List(1 -> 2, 1 -> 3, 1 -> 2, 2 -> 1, 3 -> 4, 4 -> 1, 4 -> -1, 5 -> 1)
    val ys = List(1 -> 3, 2 -> 1, 3 -> 4, 4 -> 1, 5 -> 1)
    Assert.isEquals(ys, xs.removeDuplicatesBy(_._1)(List(_, _).maxBy(_._2)).sortBy(_._1).toList)
  }

  test("Intersperse") {
    val xs = List(1, 2, 3)
    val a = 'a
    val b = 'b
    val c = 'c
    Assert.isEquals(List(a, 1, b, 2, b, 3, c), xs.intersperse(start = Some(a), sep = Some(b), end = Some(c)).toList)
  }

  test("takeUntil") {
    val xs = List(2, 23, 44, 19, 88)
    Assert.isEquals(Seq(2, 23, 44), xs.takeUntil(44 =?= _))
  }

  test("Weighted Average Opt") {
    Assert.isEquals(Some(2.25), List((1,2), (2,1), (5, 1)).weightedAvgOpt(_._1, _._2))
    Assert.isEquals(None, List((1,2), (2,-2)).weightedAvgOpt(_._1, _._2))
  }


}