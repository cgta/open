package cgta.oscala
package extensions

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/5/13 10:57 AM
//////////////////////////////////////////////////////////////


object TestTypeAExtensions extends FunSuite {

  test("oIfIs") {
    var tmp = 0
    val a = 5.oIfIs { case x: Int if x == 6 => tmp = 7}
    Assert.isEquals(5, a)
    Assert.isEquals(0, tmp)
    val b = 6.oIfIs { case x: Int if x == 6 => tmp = 7}
    Assert.isEquals(6, b)
    Assert.isEquals(7, tmp)
  }

  test("oIf") {
    Assert.isEquals("Hello true", "Hello ".oIf(!_.isEmpty, _ + "true", _ + "false"))
    Assert.isEquals("Hello false", "Hello ".oIf(_.isEmpty, _ + "true", _ + "false"))
  }

  test("OEffect") {
    var tmp = 0
    val a = 5.oEff((a) => tmp = a + 1)
    Assert.isEquals(5, a)
    Assert.isEquals(6, tmp)
  }

  test("OEffect0") {
    var tmp = 0
    val a = 5.oEff0 {tmp = 6}
    Assert.isEquals(5, a)
    Assert.isEquals(6, tmp)
  }

  test("OTransform") {
    Assert.isEquals(6, 5.oMap(_ + 1))
  }

  test("OReplace") {
    var tmp = 0
    val a = 5.oReplace {tmp = 6; 7}

    Assert.isEquals(7, a)
    Assert.isEquals(6, tmp)
  }

  test("TypeSafeEquality") {
    assert(5 =?= 5)
    assert(5 !=?= 6)
  }

  test("nullSafe") {
    val s: String = null
    Assert.isEquals(None, s.nullSafe)
    Assert.isEquals(Some(5), 5.nullSafe)
  }


}