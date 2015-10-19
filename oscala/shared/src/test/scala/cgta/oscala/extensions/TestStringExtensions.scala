package cgta.oscala
package extensions

import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/15/13 2:07 PM
//////////////////////////////////////////////////////////////

object TestStringExtensions extends FunSuite {
  object Cols {
    case object Name
    case object Role
    case object Age
    case object Weight
    case object Gender
    case object Salary
  }


  val raw = {
    import Cols._
    s"""
    $Name,   $Role,   $Age,  $Weight,   $Gender,  $Salary
    Alice,   Clerk,   27,    115,       F,        54567.45
    Bob,     Clerk,   26,    195,       M,        34567.45
    Carl,    Boss,    35,    225,       M,        84567.45
    George,  Trader,  27,    165,       M,        94567
    Deanna,  CEO,     32,    125,       F,        154567.45
    Fred,    Trader,  33,    175,       M,        104567.45
    Holly,   Trader,  24,    120,       F,        94567
    """.stripAuto
  }


  test("wrapped") {
    Assert.isEquals("[hello]", "hello".wrapped)
  }
  test("stripAutoA") {
    val s = """
      a
       b
      c
      d""".stripAuto
    Assert.isEquals("a\n b\nc\nd", s)
  }

  test("stripAutoB") {

    val s = """
      a

       b
      c
      d""".stripAuto
    Assert.isEquals("a\n\n b\nc\nd", s)
  }

  test("stripAutoTable") {
    val x = "Name,   Role,   Age,  Weight,   Gender,  Salary"
    val a = "Alice,   Clerk,   27,    115,       F,        54567.45"
    val b = "Bob,     Clerk,   26,    195,       M,        34567.45"
    val c = "Carl,    Boss,    35,    225,       M,        84567.45"
    val d = "George,  Trader,  27,    165,       M,        94567"
    val e = "Deanna,  CEO,     32,    125,       F,        154567.45"
    val f = "Fred,    Trader,  33,    175,       M,        104567.45"
    val g = "Holly,   Trader,  24,    120,       F,        94567"

    Assert.isEquals(List(x, a, b, c, d, e, f, g).mkString("\n"), raw)
  }

  test("String Interpolation") {
    Assert.isEquals("/foo/bar/13/13/foo",
      "/%FOO%/%BAR%/%N%/%N%/%FOO%".interpolate("FOO" -> "foo", "BAR" -> "bar", "N"-> 13.toString))
  }

  test("toNumerics") {
    Assert.isEquals(Some(1.0),"1.0".toDoubleOpt)
    Assert.isEquals(None,"".toDoubleOpt)
    Assert.isEquals(None,"haha".toDoubleOpt)

    Assert.isEquals(Some(1),"1".toIntOpt)
    Assert.isEquals(None,"".toIntOpt)
    Assert.isEquals(None,"haha".toIntOpt)

    Assert.isEquals(Some(1L),"1".toLongOpt)
    Assert.isEquals(None,"".toLongOpt)
    Assert.isEquals(None,"haha".toLongOpt)
  }

  test("removeEnding") {
    Assert.isEquals("foo","foo.gz".removeEnding(".gz"))
    Assert.isEquals("foo.g","foo.g".removeEnding(".gz"))
    Assert.isEquals("",".gz".removeEnding(".gz"))
    Assert.isEquals(".gz",".gz".removeEnding(""))
  }


}