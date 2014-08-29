package cgta.oscala
package util

import java.io.{ByteArrayInputStream, InputStream}

import cgta.oscala.util.IOResults.{EOF, Line, IOResult}
import cgta.otest.FunSuite

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.util.Random


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/11/14 6:45 PM
//////////////////////////////////////////////////////////////

object TestTailer extends FunSuite {
  test("Basic") {
    def generateSample: (String, Seq[IOResult[String]]) = {
      //Make a file randomly divided into segments that will be block locations
      //Make sure that the StreamTailer returns the proper combination of lines
      //Make different kinds of lines representing different places there can be pauses
      val samples = IVec(
        "0123456789\n",
        "0123456789\n",
        "0123456789\n",
        "0123456789\n",
        "0123456789p\np",
        "p0123456789p\np",
        "p01234p56789p\np",
        "p01234p56789\n",
        "01234ppp56789\n",
        "p0123456789\n"
      )
      val randy = new Random(12345)
      val file = (0 until 100).map(i => samples(randy.nextInt(samples.size))).mkString + "ppp"
      val expectedBuf = new ArrayBuffer[IOResult[String]]
      val ss = new ByteArrayInputStream(file.getBytesUTF8)
      val lineSb = new StringBuilder()

      @tailrec
      def loop() {
        val b = ss.read()
        if (b == -1) {
          //Done
          expectedBuf += EOF
        } else {
          if (b == 'p'.toByte) {
            expectedBuf += EOF
          } else if (b == '\n'.toByte) {
            expectedBuf += Line(lineSb.toString())
            lineSb.clear()
          } else {
            lineSb += b.toChar
          }
          loop()
        }
      }
      loop()

      file -> expectedBuf.toIndexedSeq
    }

    def capture(fileContent: String): Seq[IOResult[String]] = {
      var streamDone = false
      val bs = fileContent.getBytesUTF8.iterator
      val stream = new InputStream {
        override def read(): Int = {
          if (bs.hasNext) {
            val b = bs.next()
            if (b == 'p'.toByte) {
              -1
            } else {
              b
            }
          } else {
            streamDone = true
            -1
          }
        }
      }

      val tailer = new Tailer(stream)
      val res = new ArrayBuffer[IOResult[String]]
      @tailrec
      def loop() {
        val r = tailer.next()
        res += r
        //Keep looping until we have exhausted the real underlying stream
        if (!r.isEOF || !streamDone) {
          loop()
        }
      }
      loop()
      res.toIndexedSeq
    }
    val (fileContent, expected) = generateSample
    Assert.isEquals(expected, capture(fileContent))
  }

}