package cgta.oscala
package util

import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by pacifique @ 9/24/13 12:55 PM
//////////////////////////////////////////////////////////////


object TestArrayHelp extends FunSuite {
  val A = "a"
  val B = "b"
  val C = "c"
  val D = "d"
  val E = "e"
  val F = "f"
  val G = "g"
  val H = "h"
  val X = "x"
  def evens = Array(2, 4, 6, 8, 10)
  def odds = Array(1, 3, 5, 7, 9)
  def abcd = Array(A, B, C, D)
  def efgh = Array(E, F, G, H)
  def emptyIntArr = Array[Int]()
  def emptyStringArr = Array[Symbol]()

  test("RemoveAddOnLeft") {
    val arr = evens
    ArrayHelp.removeAddOnLeft(arr, 2, Int.MinValue)

    Assert.isEquals(Array(Int.MinValue, 2, 4, 8, 10).toList, arr.toList)
    assert(arr.indexOf(Int.MinValue) == 0 && !arr.contains(767))
  }

  test("RemoveAddOnRight") {
    val arr = abcd
    val removed = ArrayHelp.removeAddOnRight(arr, 1, X)
    Assert.isEquals(B, removed)
    Assert.isEquals(List(A, C, D, X), arr.toList)
  }

  test("ShiftRight") {

    def validate(amount: Int, newVal: Int, result: Array[Int]) {

      val arr = evens
      ArrayHelp.shiftRight(arr, amount, newVal)
      Assert.isEquals(result.toList, arr.toList)

    }

    validate(0, 17, Array(2, 4, 6, 8, 10))
    validate(2, 17, Array(17, 17, 2, 4, 6))
    validate(-10, 0, Array(0, 0, 0, 0, 0))
    validate(5, 0, Array(0, 0, 0, 0, 0))

  }

  test("ShiftLeft") {
    val arr = (1 to 10).toArray
    ArrayHelp.shiftLeft(arr, 0, 19)
    assert(!arr.contains(19))
    ArrayHelp.shiftLeft(arr, 2, 15)
    assert(arr.filter(_ == 15).length == 2)
    ArrayHelp.shiftLeft(arr, -30, 19)
    assert(arr.sum == 19 * arr.length)

  }

  test("InsertAt") {

    def validate(idx: Int, popped: String, result: List[String]) {
      val xs = abcd
      Assert.isEquals(popped, ArrayHelp.insertAt(xs, idx, X))
      Assert.isEquals(result, xs.toList)
    }

    validate(0, D, List(X, A, B, C))
    validate(1, D, List(A, X, B, C))
    validate(2, D, List(A, B, X, C))
    validate(3, D, List(A, B, C, X))
    Assert.intercepts[IndexOutOfBoundsException](ArrayHelp.insertAt(abcd, 4, X))
    Assert.intercepts[IndexOutOfBoundsException](ArrayHelp.insertAt(abcd, -1, X))
  }

  test("InsertAtShiftLeft") {
    def validate(idx: Int, popped: String, result: List[String]) {
      val xs = abcd
      Assert.isEquals(popped, ArrayHelp.insertAtShiftLeft(xs, idx, X))
      Assert.isEquals(result, xs.toList)
    }

    validate(0, A, List(X, B, C, D))
    validate(1, A, List(B, X, C, D))
    validate(2, A, List(B, C, X, D))
    validate(3, A, List(B, C, D, X))
    Assert.intercepts[IndexOutOfBoundsException](ArrayHelp.insertAt(abcd, 4, X))
    Assert.intercepts[IndexOutOfBoundsException](ArrayHelp.insertAt(abcd, -1, X))
  }

  test("Grow") {
    val arr = (1 to 5).toArray
    Assert.isEquals(10, ArrayHelp.grow(arr, 10).length)

  }

  test("CopyNew") {
    val arr = evens
    Assert.isEquals(arr.toList, ArrayHelp.copyNew(arr).toList)
  }


}