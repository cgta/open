package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/14/15 9:05 AM
//////////////////////////////////////////////////////////////

class ThrowableExtensions(val t : Throwable) extends AnyVal {

  @inline def rethrow(msg : String = "") {
    if (msg.nonEmpty) {
      throw new RuntimeException(msg, t)
    } else {
      throw new RuntimeException(t)
    }
  }

}