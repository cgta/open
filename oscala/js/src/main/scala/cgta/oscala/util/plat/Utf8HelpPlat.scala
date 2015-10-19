package cgta.oscala
package util.plat

import cgta.oscala.impls.Utf8Converter
import cgta.oscala.util.Utf8Help


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/16/14 1:02 PM
//////////////////////////////////////////////////////////////

trait Utf8HelpPlat extends Utf8Help {
  override def toBytes(s: String): Array[Byte] = Utf8Converter.toBytes(s)
  override def fromBytes(bs: Array[Byte]): String = Utf8Converter.fromBytes(bs)
}