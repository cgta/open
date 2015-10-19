package cgta.oscala
package extensions

import cgta.oscala.util.Utf8Help


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/10/15 5:44 PM
//////////////////////////////////////////////////////////////

class ByteArrayExtensions(val bs : Array[Byte]) extends AnyVal {
  def toStringUTF8: String = Utf8Help.fromBytes(bs)
}