package cgta.oscala
package util

import java.io.InputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/23/15 7:27 PM
//////////////////////////////////////////////////////////////

class ThrottledInputStream(rawStream: InputStream, kBytesPerSec: Int) extends InputStream {
  var totalBytesRead  = 0L
  var startTimeMillis = 0L

  val BYTES_PER_KILOBYTE = 1024
  val MILLIS_PER_SECOND  = 1000
  val ratePerMillis      = kBytesPerSec * BYTES_PER_KILOBYTE / MILLIS_PER_SECOND;

  override def read(): Int = {
    if (startTimeMillis == 0) {
      startTimeMillis = System.nanoTime() / Million
    }
    val now = System.nanoTime() / Million
    val interval = now - startTimeMillis
    //see if we are too fast..
    if (interval * ratePerMillis < totalBytesRead + 1) {
      //+1 because we are reading 1 byte
      val sleepTime = ratePerMillis / (totalBytesRead + 1) - interval // will most likely only be relevant on the first few passes
      Thread.sleep(Math.max(1, sleepTime))
    }
    totalBytesRead += 1
    rawStream.read()
  }

  override def close() {
    rawStream.close()
  }
}