package cgta.oscala
package util

import cgta.oscala.util.plat.Utf8HelpPlat


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/11/14 6:11 PM
//////////////////////////////////////////////////////////////

object Utf8Help extends Utf8Help with Utf8HelpPlat

trait Utf8Help {
  def toBytes(s: String): Array[Byte]
  def fromBytes(bs: Array[Byte]): String
}

