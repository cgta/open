package cgta.oscala
package extensions

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:52 PM
//////////////////////////////////////////////////////////////


class ByteExtensions(val x: Byte) extends AnyVal {
  def toUInt32: Int = x & 0xFF
}