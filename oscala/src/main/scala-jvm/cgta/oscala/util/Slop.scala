package cgta.oscala
package util

import java.io.FileOutputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/14/14 12:22 AM
//////////////////////////////////////////////////////////////


object Slop {
  def asFile(filename: String, data: String, append: Boolean) {
    asFile(filename, data.getBytesUTF8, append)
  }
  def asFile(filename: String, data: Array[Byte], append: Boolean) {
    Closing(new FileOutputStream(filename, append)) { fos =>
      fos.write(data)
      fos.flush()
    }
  }

}