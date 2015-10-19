package cgta.oscala
package util

import java.io.File
import java.io.RandomAccessFile


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/12/15 3:31 PM
//////////////////////////////////////////////////////////////

object FileCopier {
  def partialSync(src: File, dest: File) {
    if (src.exists() && src.isFile) {
      Closing(new RandomAccessFile(src, "r").getChannel, new RandomAccessFile(dest, "rw").getChannel) { (cin, cout) =>
        val nextOffset = cout.size
        cin.position(nextOffset)
        val cnt = cin.size() - nextOffset
        if (cnt > 0) {
          cout.transferFrom(cin, nextOffset, cnt)
        }
      }
    }
  }

  def main(args: Array[String]) {
    val a = args(0).toFile
    val b = args(1).toFile
    while (b.length() < a.length()) {
      partialSync(a, b)
      Thread.sleep(1)
    }
  }
}


