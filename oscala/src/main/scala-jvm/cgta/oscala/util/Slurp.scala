package cgta.oscala
package util

import java.io.{FileInputStream, ByteArrayOutputStream, OutputStream, InputStream}


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/14/14 12:17 AM
//////////////////////////////////////////////////////////////

object Slurp {
  def pipe(is: InputStream, os: OutputStream) {
    val buf = new Array[Byte](1024)
    try {
      var c = is.read(buf)
      while (c > 0) {
        os.write(buf, 0, c)
        c = is.read(buf)
      }
    } finally {
      is.close()
    }
  }

  def asBytes(is: InputStream): Array[Byte] = {
    val buffer = new ByteArrayOutputStream()
    pipe(is, buffer)
    buffer.toByteArray
  }

  def asString(is: InputStream): String = {
    new String(asBytes(is), UTF8)
  }

  def asString(filename: String): String = {
    val fis = new FileInputStream(filename)
    try {
      new String(asBytes(fis), UTF8)
    } finally {
      fis.close()
    }
  }

  def tailer(is : InputStream) = new Tailer(is)

}



