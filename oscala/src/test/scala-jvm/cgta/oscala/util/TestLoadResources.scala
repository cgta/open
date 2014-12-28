package cgta.oscala.util

import cgta.oscala.util.debugging.PRINT
import cgta.otest.FunSuite

import scala.io.Source


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/11/14 12:41 PM
//////////////////////////////////////////////////////////////

object TestLoadResources extends FunSuite {
  ignore("Load with class loader") {
    val rPath = "oscala-sample-resource.txt"
    val r = Source.fromURL(getClass.getClassLoader.getResource(rPath))
    Assert.isEquals("Testing 123", r.getLines().toList.mkString("\n"))

  }
}