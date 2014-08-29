package cgta.oscala
package util

import java.io.{ByteArrayOutputStream, InputStream}

import cgta.oscala.util.IOResults.{EOF, Line, IOResult}

import scala.annotation.tailrec


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/11/14 4:55 PM
//////////////////////////////////////////////////////////////


class Tailer(is: InputStream) {
  var baos = new ByteArrayOutputStream()

  //If a file does not end with '\n' the last line will not be returned
  //It can be obtained with peek
  @tailrec
  final def next(): IOResult[String] = {
    val bi = is.read()
    if (bi == -1) {
      //AT EOF
      EOF
    } else {
      if (bi.toByte == '\n'.toByte) {
        val r = Line(peek)
        baos = new ByteArrayOutputStream()
        r
      } else {
        baos.write(bi)
        next()
      }
    }
  }

  //Returns the contents of the current line
  def peek: String = Utf8Help.fromBytes(baos.toByteArray)

}