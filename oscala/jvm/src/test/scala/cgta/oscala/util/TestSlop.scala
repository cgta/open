package cgta.oscala
package util

import java.io.ByteArrayInputStream
import java.io.File

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/7/15 11:43 AM
//////////////////////////////////////////////////////////////

object TestSlop extends FunSuite {

  test("SlopToFile") {
    val tf = File.createTempFile("test", "xx")
    try {
      val s = "this is just a test"
      val bais = new ByteArrayInputStream(s.getBytesUTF8)
      Slop.toFile(tf, bais)
      Assert.isEquals(s, Slurp.asString(tf))
    } finally {
      tf.delete()
    }
  }

}