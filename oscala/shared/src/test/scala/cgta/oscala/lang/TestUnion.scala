package cgta.oscala
package lang

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/10/15 9:44 AM
//////////////////////////////////////////////////////////////

object TestUnion extends FunSuite {

  import Union._

  test("UnionCompiles") {
    val a: Int | String = 5
    val b: Int | String = "hello"
    //val x: Int | String = false // does not compile
    Assert.isEquals("5", a.toString)
    Assert.isAnyEquals(5, a)
    Assert.isAnyEquals("hello", b)

    val c: Int | Boolean | String = a
    val num: Int | Double = 5
    val any: AnyVal = num.merge
    Assert.isAnyEquals(5, any)
    //val y: Int | String = c // does not compile

    val d: String | AnyVal = c
    Assert.isAnyEquals(5, d)
    val e: Boolean | String | Int = c
    Assert.isAnyEquals(5, e)

    e match {
      case x: Int =>
      case x => Assert.fail("Should be int")
    }
  }

}