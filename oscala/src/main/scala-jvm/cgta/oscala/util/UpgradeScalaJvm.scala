package cgta.oscala
package util

import java.io.BufferedReader
import java.io.InputStreamReader


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 12/6/14 10:11 PM
//////////////////////////////////////////////////////////////

object UpgradeScalaJvm {
  lazy val lineReader = new BufferedReader(new InputStreamReader(System.in))
  def readLine() = lineReader.readLine()
}