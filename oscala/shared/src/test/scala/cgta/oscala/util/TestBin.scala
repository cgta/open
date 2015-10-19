package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/23/13 5:44 PM
//////////////////////////////////////////////////////////////


object TestBin extends FunSuite {

  case class TestException(msg: String) extends Exception {
    override def toString() = s"TestException($msg)"
  }

  test("Attempt") {
    Assert.isEquals(BinSome(5), Bin.attempt(5))
    Assert.isEquals(BinNone(BinError(TestException("X"))), Bin.attempt(throw TestException("X")))
  }

  test("Get") {
    Assert.isAnyEquals(5, BinSome(5).get)
    Assert.intercepts[NoSuchElementException](BinNone().get)
  }

  test("GetError") {
    Assert.intercepts[NoSuchElementException](BinSome(5).getError)
    Assert.intercepts[NoSuchElementException](BinNone().getError)
    Assert.isEquals(BinError("X"), BinNone(BinError("X")).getError)
  }

  test("Error") {
    Assert.isEquals(Some(BinError("X")), BinNone(BinError("X")).error)
    Assert.isEquals(None, BinNone().error)
    Assert.isEquals(None, BinSome(5).error)
  }

  test("WithMsg") {
    Assert.isEquals(BinNone("Y"), BinNone(BinError("X")).setMsg("Y"))
    Assert.isEquals(BinSome(5), BinSome(5).setMsg("Y"))
    Assert.isEquals(BinNone("Y"), BinNone().setMsg("Y"))
  }

  test("GetOrElse") {
    Assert.isEquals(5, BinSome(5).getOrElse(4))
    Assert.isEquals(4, BinNone().getOrElse(4))
  }

  test("OrElse") {
    Assert.isEquals(BinSome(5), BinSome(5).orElse(BinSome(6)))
    Assert.isEquals(BinSome(6), BinNone().orElse(BinSome(6)))
  }

  test("Filter") {
    Assert.isEquals(BinSome(5), BinSome(5).filter(5 == _).filter(5 == _))
    Assert.isEquals(BinNone(), BinSome(5).filter(5 == _).filter(6 == _))
    Assert.isEquals(BinNone(), BinSome(5).filter(6 == _).filter(5 == _))
    Assert.isEquals(BinNone(), BinSome(5).filter(6 == _))
    Assert.isEquals(BinNone(), BinNone().filter(_ == 5))
  }

  test("WithFilter") {
    Assert.isEquals(BinSome(5), for {x <- BinSome(5) if x == 5} yield x)
    Assert.isEquals(BinSome(5), for {x <- BinSome(5) if x == 5 if x == 5} yield x)
    Assert.isEquals(BinNone(), for {x <- BinSome(5) if x == 6 if x == 5} yield x)
    Assert.isEquals(BinNone(), for {x <- BinSome(5) if x == 5 if x == 6} yield x)
    Assert.isEquals(BinNone(), for {x <- BinSome(5) if x == 6} yield x)
  }

  test("BinNoneApplys") {
    Assert.isEquals(BinNone(None), BinNone())
    Assert.isEquals(BinNone(Some(BinError("Y"))), BinNone("Y"))
    Assert.isEquals(BinNone(Some(BinError(TestException("Y")))), BinNone(TestException("Y")))
  }

  test("BinMultiOk") {
    val z = for {x <- Bin.attempt(5)
                 y <- Bin.attempt(6)} yield {
      x -> y
    }
    Assert.isEquals(BinSome(5 -> 6), z)
  }

  test("BinFilterNone") {
    val z = for {x <- Bin.attempt(5)
                 if x == 6
                 y <- Bin.attempt(6)} yield {
      x -> y
    }
    Assert.isEquals(BinNone(), z)
  }

  test("BinMultiError1") {
    val z = for {x <- Bin.attempt[Int](throw TestException("x"))
                 y <- Bin.attempt[Int](throw TestException("y"))} yield {
      x -> y
    }
    Assert.isEquals(BinNone(TestException("x")), z)
  }

  test("BinMultiError2") {
    val z = for {x <- Bin.attempt[Int](5)
                 y <- Bin.attempt[Int](throw TestException("y"))} yield {
      x -> y
    }
    Assert.isEquals(BinNone(TestException("y")), z)
  }

}