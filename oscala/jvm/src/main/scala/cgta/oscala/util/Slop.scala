package cgta.oscala
package util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/14/14 12:22 AM
//////////////////////////////////////////////////////////////


object Slop {
  def asFile(file: File, data: String, append: Boolean) {
    asFile(file, data.getBytesUTF8, append)
  }
  def asFile(file: File, data: Array[Byte], append: Boolean) {
    Closing(new FileOutputStream(file, append)) { fos =>
      fos.write(data)
      fos.flush()
    }
  }
  def asFile(filename: String, data: String, append: Boolean) {
    asFile(filename, data.getBytesUTF8, append)
  }
  def asFile(filename: String, data: Array[Byte], append: Boolean) {
    asFile(filename.toFile, data, append)
  }

  def toFile(file: File, inputStream: InputStream) {
    val buf = new Array[Byte](1024 * 1024)
    Closing(new FileOutputStream(file)) { fos =>
      var done = false
      while (!done) {
        val c = inputStream.read(buf)
        if (c == -1) {
          done = true
        } else if (c > 0) {
          fos.write(buf, 0, c)
        }
      }
    }
  }

  val newline = '\n'.toByte

  def lines(file: File, itr : TraversableOnce[String], append : Boolean) {
    Closing(new FileOutputStream(file, append)) { fos =>
      itr.foreach { line =>
        fos.write(line.getBytesUTF8)
        fos.write(newline)
      }
      fos.flush()
    }
  }
}