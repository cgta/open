package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by kklipsch @ 9/23/13 11:18 AM
//////////////////////////////////////////////////////////////

object TestCloseable extends FunSuite {

  private trait TestClose {
    var isClosed = false
  }

  private class TCloseable extends TestClose with Closable {
    def close() {
      isClosed = true
    }
  }

  private class TExploder extends Closable {
    def close() {
      throw new Exception()
    }
  }

  private class TStructuralClose extends TestClose {
    def close() {
      isClosed = true
    }
  }

  private class TTypeClass extends TestClose

  private implicit object TTypeClassCloser$ extends Closer[TTypeClass] {
    def close(a: TTypeClass) {
      a.isClosed = true
    }
  }

  test("Closing1") {
    val a1 = new TCloseable
    Closing(a1) {
      a =>
        Assert.isTrue(!a.isClosed)
    }
    Assert.isTrue(a1.isClosed)
  }

  test("Closing2") {
    val a1 = new TCloseable
    val b1 = new TTypeClass
    Closing(a1, b1) {
      (a, b) =>
        Assert.isTrue(!a.isClosed)
        Assert.isTrue(!b.isClosed)
    }

    Assert.isTrue(a1.isClosed)
    Assert.isTrue(b1.isClosed)
  }

  test("Closing3") {
    val a1 = new TStructuralClose
    val b1 = new TTypeClass
    val c1 = new TCloseable

    Closing(a1, b1, c1) {
      (a, b, c) =>
        Assert.isTrue(!a.isClosed)
        Assert.isTrue(!b.isClosed)
        Assert.isTrue(!c.isClosed)
    }

    Assert.isTrue(a1.isClosed)
    Assert.isTrue(b1.isClosed)
    Assert.isTrue(c1.isClosed)
  }

  test("ClosingWithException") {
    val a1 = new TStructuralClose
    val b1 = new TTypeClass
    val c1 = new TCloseable

    Assert.intercepts[Exception] {
      Closing(a1, b1, c1) {
        (a, b, c) =>
          throw new Exception()
      }
    }

    Assert.isTrue(a1.isClosed)
    Assert.isTrue(b1.isClosed)
    Assert.isTrue(c1.isClosed)

  }

  test("ExceptionInClosing") {
    val a1 = new TStructuralClose
    val b1 = new TTypeClass
    val c1 = new TCloseable

    Assert.intercepts[Exception] {
      Closing(a1, b1, new TExploder) {
        (a, b, c) =>
      }
    }

    Assert.isTrue(a1.isClosed)
    Assert.isTrue(b1.isClosed)

    a1.isClosed = false
    b1.isClosed = false

    Assert.intercepts[Exception] {
      Closing(a1, new TExploder, c1) {
        (a, b, c) =>
      }
    }

    Assert.isTrue(a1.isClosed)
    Assert.isTrue(c1.isClosed)

    a1.isClosed = false
    c1.isClosed = false

    Assert.intercepts[Exception] {
      Closing(new TExploder, b1, c1) {
        (a, b, c) =>
      }
    }

    Assert.isTrue(b1.isClosed)
    Assert.isTrue(c1.isClosed)
  }


}