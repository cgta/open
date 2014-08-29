package cgta.cenum


import cgta.serland.testing.UnitTestHelpSerClass
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/8/14 3:55 PM
//////////////////////////////////////////////////////////////


object TestCEnum extends FunSuite with UnitTestHelpSerClass {
  private object Fruits extends CEnum {
    type EET = Fruit
    sealed abstract class Fruit(color: String, isSweet: Boolean) extends EnumElement
    case object Apple extends Fruit("red", true)
    case object Orange extends Fruit("orange", true)
    case object Pear extends Fruit("green", true)
    case object Banana extends Fruit("yellow", true)
    case object Tomato extends Fruit("red", false)

    override val elements = CEnum.getElements(this)
  }

  import TestCEnum.Fruits.Fruit
  test("toIVec") {
    Assert.isAnyEquals(Fruits.toIVec, IVec(Fruits.Apple, Fruits.Orange, Fruits.Pear, Fruits.Banana, Fruits.Tomato))
  }

  test("toIMap") {
    Assert.isAnyEquals(Fruits.Apple, Fruits.toIMap("Apple"))
    Assert.isAnyEquals(Fruits.Orange, Fruits.toIMap("Orange"))
    Assert.isAnyEquals(Fruits.Pear, Fruits.toIMap("Pear"))
    Assert.isAnyEquals(Fruits.Banana, Fruits.toIMap("Banana"))
    Assert.isAnyEquals(Fruits.Tomato, Fruits.toIMap("Tomato"))
  }

  test("ordering") {
    val unordered: Vector[Fruit] = IVec(Fruits.Orange, Fruits.Pear, Fruits.Apple, Fruits.Banana, Fruits.Tomato)
    Assert.isEquals(Fruits.toIVec, unordered.sorted)
  }

  test("serClass") {
    val q = "\""
    validate[Fruits.Fruit](Fruits.Apple)(q + Fruits.Apple + q)
    validate[Fruits.Fruit](Fruits.Orange)(q + Fruits.Orange + q)
    validate[Fruits.Fruit](Fruits.Pear)(q + Fruits.Pear + q)
    validate[Fruits.Fruit](Fruits.Banana)(q + Fruits.Banana + q)
    validate[Fruits.Fruit](Fruits.Tomato)(q + Fruits.Tomato + q)
  }
}



