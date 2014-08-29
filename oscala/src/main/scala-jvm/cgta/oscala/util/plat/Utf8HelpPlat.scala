package cgta.oscala
package util.plat

import cgta.oscala.util.Utf8Help

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/11/14 6:08 PM
//////////////////////////////////////////////////////////////

trait Utf8HelpPlat extends Utf8Help {
  override def toBytes(s: String): Array[Byte] = s.getBytes("UTF-8")
  override def fromBytes(bs: Array[Byte]): String = new String(bs, "UTF-8")
}