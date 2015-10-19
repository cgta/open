package cgta.oscala
package util

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/14/14 12:17 AM
//////////////////////////////////////////////////////////////

object Slurp {
  //alias(slurpToStream)
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

  def asBytes(filename: File): Array[Byte] = {
    val buffer = new ByteArrayOutputStream()
    pipe(new FileInputStream(filename), buffer)
    buffer.toByteArray
  }


  def asString(is: InputStream): String = {
    Utf8Help.fromBytes(asBytes(is))
  }

  def asString(f: File): String = {
    val fis = new FileInputStream(f)
    try {
      Utf8Help.fromBytes(asBytes(fis))
    } finally {
      fis.close()
    }
  }

  def asString(filename: String): String = {
    val fis = new FileInputStream(filename)
    try {
      Utf8Help.fromBytes(asBytes(fis))
    } finally {
      fis.close()
    }
  }

  def lines(f: File): Iterator[String] with Closable = lines(new FileReader(f))

  def lines(ins: InputStream): Iterator[String] with Closable = lines(new InputStreamReader(ins))

  def lines(r: Reader): Iterator[String] with Closable = {
    val reader = new BufferedReader(r)
    val itr = IteratorHelp.fromFunction[String](() => reader.readLine().nullSafe)

    new Iterator[String] with Closable {
      override def hasNext: Boolean = itr.hasNext
      override def next(): String = itr.next()
      override def close(): Unit = reader.close()
    }

  }
}



