package cgta.oscala
package util

import java.io.File
import java.io.FileOutputStream

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/12/15 3:46 PM
//////////////////////////////////////////////////////////////

object TestFileCopier extends FunSuite {

  test("basic") {
    val a = File.createTempFile("unit-test", "in")
    a.delete()
    val b = File.createTempFile("unit-test", "out")
    b.delete()

    def s = FileCopier.partialSync(a, b)

    Closing(new FileOutputStream(a)) { os =>
      s
      Assert.isEquals("", Slurp.asString(b))
      os.write("1".getBytesUTF8)
      s
      s
      Assert.isEquals("1", Slurp.asString(b))
      os.write("2".getBytesUTF8)
      s
      s
      Assert.isEquals("12", Slurp.asString(b))
      s
      Assert.isEquals("12", Slurp.asString(b))
    }
  }

}