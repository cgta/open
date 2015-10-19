package cgta.serland

import cgta.otest.FunSuite
import cgta.serland.backends.SerJsonIn


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/1/14 2:17 PM
//////////////////////////////////////////////////////////////


//Known bugs go here when working around them
object TestSerlandBugs extends FunSuite {

  object Foo {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class Foo(z: Int)

  object RequiredFields {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class RequiredFields(x: Int, y: Foo)

  test("Required fields fail fast") {
    def v(name: String)(f: => Unit) {
      try {
        f
        Assert.fail("Should not have parsed!")
      } catch {
        case t: Throwable =>
          def cause(e: Throwable): Throwable = {
            val c = e.getCause
            if (c == null) e else cause(c)
          }
          val e = cause(t)
          val expectedStr = s"Missing Required Field: $name"
          Assert.isTrue(e.toString.contains(expectedStr), "Expected: " + expectedStr, "Actual: " + e)
      }
    }
    v("x")(SerJsonIn.fromJsonString[RequiredFields]("""{}"""))
    v("y")(SerJsonIn.fromJsonString[RequiredFields]("""{"x":1}"""))
  }

  test("Don't Escape forward slash") {
    Assert.isEquals("\"/a/b/c\"", "/a/b/c".toJsonCompact())
  }

  test("Integers don't add .0") {
    val i = 10L
    Assert.isEquals("10", i.toJsonCompact())
  }
}