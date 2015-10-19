package cgta.oscala
package util

import java.io.File

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/11/14 4:53 PM
//////////////////////////////////////////////////////////////

object TestSlurp extends FunSuite {
  ignore("WRITE MORE SLURP TESTS") {

  }

  test("lines") {
    val f = File.createTempFile("test", ".txt")
    Slop.asFile(f.getPath, "1\n2\n3", append = false)
    try {
      Assert.isEquals(List("1", "2", "3"), Slurp.lines(f).toList)

    } finally {
      f.delete()
    }
  }
}