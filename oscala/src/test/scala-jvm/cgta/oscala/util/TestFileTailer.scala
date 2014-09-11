package cgta.oscala
package util

import java.io.{FileOutputStream, File, ByteArrayInputStream, InputStream}

import util.IOResults.{IOData, EOF, IOResult}
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

object TestFileTailer extends FunSuite {
  test("Basic") {
    def generateSample: (String, IVec[IOResult[IVec[String]]]) = {
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
      val expectedBuf = new ArrayBuffer[IOResult[IVec[String]]]
      val groupBuf = new ArrayBuffer[String]
      val ss = new ByteArrayInputStream(file.getBytesUTF8)
      val lineSb = new StringBuilder()

      def retireGroupBuf() {
        if (groupBuf.nonEmpty) {
          expectedBuf += IOData(groupBuf.toVector)
          groupBuf.clear()
        }
      }


      @tailrec
      def loop() {
        val b = ss.read()
        if (b == -1) {
          //Done
          retireGroupBuf()
          expectedBuf += EOF
        } else {
          if (b == 'p'.toByte) {
            retireGroupBuf()
            expectedBuf += EOF
          } else if (b == '\n'.toByte) {
            groupBuf += lineSb.toString()
            lineSb.clear()
          } else {
            lineSb += b.toChar
          }
          loop()
        }
      }
      loop()

      file -> expectedBuf.toVector
    }

    def capture(fileContent: String): IVec[IOResult[IVec[String]]] = {
      var streamDone = false
      val bs = fileContent.getBytesUTF8.iterator
      val tmpFile = File.createTempFile("unit-test", "tmp")
      val fos = new FileOutputStream(tmpFile)
      @tailrec
      def advanceStream() {
        if (bs.hasNext) {
          val b = bs.next()
          if (b == 'p'.toByte) {
          } else {
            fos.write(b)
            advanceStream()
          }
        } else {
          streamDone = true
        }
      }
      val tailer = new FileTailer(tmpFile.getPath)
      val res = new ArrayBuffer[IOResult[IVec[String]]]
      @tailrec
      def loop() {
        advanceStream()
        def loop2() {
          val r = tailer.next().map(lines => lines.map(Utf8Help.fromBytes _))
          res += r
          if (!r.isEOF) {
            loop2()
          }
        }
        loop2()
        //Keep looping until we have exhausted the real underlying stream
        if (!streamDone) {
          loop()
        }
      }
      loop()
      res.toVector
    }
    val (fileContent, expected) = generateSample
    Assert.isEquals(expected, capture(fileContent))
  }

}