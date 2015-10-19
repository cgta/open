package cgta.serland

import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/24/13 11:18 PM
//////////////////////////////////////////////////////////////


object TestMultiCtor extends FunSuite {
  private case class Defaults(x: Int = 1, y: Int = 2)
  private case class MultiCtor0ArgsDefault(x: Int, y: Int) {
    def this() = this(1, 2)
  }
  private case class MultiCtor1ArgExtra(x: Int, y: Int) {
    def this(x: Int) = this(x, 2)
  }


  test("DefaultsAndMultiCtors") {
    //Yes, I guess I am unit testing the constructor.
    def validate[A: SerClass](x: => A, y: => A) {
      Assert.isEquals("""{"x":1,"y":2}""" ,  x.toJsonCompact)
      Assert.isEquals(x ,  x.toJsonCompact.fromJson[A])
      Assert.isEquals(y ,  y.toJsonCompact.fromJson[A])
    }
    validate(Defaults(), Defaults(5))(SerBuilder.forCase(Defaults.apply _))
    validate(new MultiCtor0ArgsDefault(), MultiCtor0ArgsDefault(5, 6))(SerBuilder.forCase(MultiCtor0ArgsDefault.apply _))
    validate(new MultiCtor1ArgExtra(1), MultiCtor1ArgExtra(5, 6))(SerBuilder.forCase(MultiCtor1ArgExtra.apply _))
  }

}