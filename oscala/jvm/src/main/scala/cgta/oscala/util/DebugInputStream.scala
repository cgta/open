package cgta.oscala
package util

import java.io.InputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/4/15 3:19 PM
//////////////////////////////////////////////////////////////

class DebugInputStream(that: InputStream, log: (String) => Unit) extends InputStream {
  var c = 0L
  var t = 0L
  override def read(): Int = {
    c += 1
    if (c >= 30 * Million) {
      t += c
      c = 0
      log(s"Total Uploaded MBytes: ${t / Million}")
    }
    that.read()
  }
  override def close() { that.close() }
}