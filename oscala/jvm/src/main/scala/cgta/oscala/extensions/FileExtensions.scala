package cgta.oscala
package extensions

import java.io.File


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/6/15 2:47 PM
//////////////////////////////////////////////////////////////

class FileExtensions(val f : File) extends AnyVal{

  def /(that: String) : File = {
    if (f.toString.endsWith("/")) {
      new File(f + that)
    } else {
      new File(f + "/" + that)
    }

  }

}