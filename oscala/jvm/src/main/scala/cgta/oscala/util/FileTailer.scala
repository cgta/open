package cgta.oscala
package util

import java.io.{RandomAccessFile, File}
import java.nio.ByteBuffer

import util.IOResults.{IOData, EOF, IOResult}

import annotation.tailrec
import collection.mutable.ArrayBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/6/14 6:32 AM
//////////////////////////////////////////////////////////////


/**
 * Designed to read in a dump file upto a max number of lines per pull
 * if maxLines <= 0 then it just reads an unlimited amount at a time.
 *
 */
class FileTailer(val fname: String, val maxLines: Int = 10000, val delimiter: Byte = "\n".getBytesUTF8(0)) {
  var nextOffset: Long = 0
  def getInput: Option[RandomAccessFile] = {
    val f = new File(fname)
    if (f.exists()) {
      Some(new RandomAccessFile(fname, "r"))
    } else {
      None
    }
  }
  var _bbuf = allocate(20 * Thousand.toInt)

  private def allocate(n: Int) = ByteBuffer.wrap(new Array[Byte](n))


  private def appendByte(b: Byte) {
    if (_bbuf.remaining() <= 0) {
      val nbbuf = allocate(_bbuf.capacity() * 2)
      _bbuf.flip()
      nbbuf.put(_bbuf)
      _bbuf = nbbuf
    }
    _bbuf.put(b)
  }

  def next(): IOResult[IVec[Array[Byte]]] = {
    var ret = new ArrayBuffer[Array[Byte]]()
    var fnf = false
    def moveOutLine() {
      _bbuf.flip()
      val tarr = new Array[Byte](_bbuf.limit())
      _bbuf.get(tarr)
      _bbuf.clear()
      ret += tarr
    }
    getInput match {
      case Some(i) => Closing(i) { raf =>
        if (nextOffset < raf.length()) {
          raf.seek(nextOffset)
          //TODO Read in batches to speed this up significantly
          @tailrec
          def loop() {
            if (ret.size < maxLines || maxLines <= 0) {
              val r = raf.read()
              if (r < 0) {
                //EOF
              } else {
                nextOffset += 1
                val b = r.toByte
                if (b == delimiter) {
                  moveOutLine()
                  loop()
                } else {
                  appendByte(b.toByte)
                  loop()
                }
              }
            }
          }
          loop()
        }
      }
      case None => fnf = true
    }

    if (fnf) EOF
    else if (ret.isEmpty) EOF
    else IOData(ret.toVector)
  }
}