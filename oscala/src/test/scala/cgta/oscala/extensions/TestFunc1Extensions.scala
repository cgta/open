package cgta.oscala
package extensions

import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/23/14 9:11 AM
//////////////////////////////////////////////////////////////


object TestFunc1Extensions extends FunSuite {

  test("ToPartialFunction") {
    val f = (x: Int) => x + 1
    Assert.isEquals(2, f.toPartial(1))
    Assert.isEquals(true, f.toPartial.isDefinedAt(1))
  }

}