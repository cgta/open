package cgta.oscala
package extensions

import cgta.otest.FunSuite

import scala.collection.mutable.ArrayBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 2/4/15 1:34 PM
//////////////////////////////////////////////////////////////

object TestArrayBufferExtensions extends FunSuite {

  test("sparseSet") {
    def x(xs : Int*)(f : ArrayBuffer[Int] => Unit) {
      val buf = new ArrayBuffer[Int]()
      f(buf)
      Assert.isEquals(xs.toList, buf.toList)
    }

    x(1,4,3) { buf =>
      buf += 1
      buf += 2
      buf += 3
      buf.setSparse(i = 1, 4, -1)
    }

    x(1, 4) { buf =>
      buf.setSparse(i = 0, 1, -1)
      buf.setSparse(i = 1, 4, -1)
    }

    x(-1, 1) { buf =>
      buf.setSparse(i = 1, 1, -1)
    }


  }

}