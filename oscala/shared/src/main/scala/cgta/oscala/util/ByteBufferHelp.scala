package cgta.oscala
package util

import java.nio.ByteBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by kklipsch @ 4/17/14 9:06 AM
//////////////////////////////////////////////////////////////


object ByteBufferHelp {



  def getRemainingAsByteArray(buffer: ByteBuffer): Array[Byte] = {
    val dst = new Array[Byte](buffer.remaining())
    buffer.get(dst)
    dst
  }

  /**
   * Expand the given buffer to a new capacity if it's greater than the buffer's
   *
   */
  def expanded(buffer: ByteBuffer, newCapacity: Int): ByteBuffer = {
    if (newCapacity < buffer.capacity()) {
      buffer
    } else {
      buffer.flip()
      val bb = ByteBuffer.allocate(newCapacity)
      bb.put(buffer)
      bb
    }
  }

  //TODO FIX BB
  def putIntAsString(i : Int, bb : ByteBuffer): Unit = ???
  def putLongAsString(l: Long, buffer: ByteBuffer): Unit = ???
  def putStringAsString(s: String, buffer: ByteBuffer): Unit = ???
//  def putDeciAsString(s: Deci, buffer: ByteBuffer): Unit = ???
}