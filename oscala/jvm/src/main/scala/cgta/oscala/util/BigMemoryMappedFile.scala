package cgta.oscala
package util

import java.io.File
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

import scala.collection.mutable.ArrayBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/7/15 3:24 PM
//////////////////////////////////////////////////////////////

object BigMemoryMappedFile {
  def apply(f: File, maxContiguousReadSz: Long): BigMemoryMappedFile = {
    require(maxContiguousReadSz <= Million)
    val fLen = f.length
    if (f.length < Int.MaxValue) {
      //simple case
      val chunk = makeChunk(f, startOffset = 0, sz = fLen)
      new BigMemoryMappedFile {
        override def getAddr(offset: Long): Long = chunk.addr + offset
        override def close() = chunk.close()
        override val length = fLen
      }
    } else {
      val chunkSz: Long = 2 * Billion
      val buf = ArrayBuffer.empty[Chunk]
      def loop(start: Long) {
        if (start < fLen) {
          buf += makeChunk(f, startOffset = (start - maxContiguousReadSz).max(0L), sz = chunkSz)
          loop(start + chunkSz)
        }
      }
      loop(0)

      if (buf.isEmpty) {
        sys.error(s"This should be unreachable but cannot memory map an empty file! $f")
      } else {
        new BigMemoryMappedFile {
          private val chunks: Array[Chunk] = buf.toArray
          //This is just an optimization
          private var lastChunkIdx = 0
          private val chunkStarts: Array[Long] = chunks.map(_.startOffset)
          private val chunkEnds: Array[Long] = (chunkStarts.toList.tail ::: List(Long.MaxValue)).toArray

          override def getAddr(offset: Long): Long = {
            val chunkIdx: Int = {
              val lci = lastChunkIdx
              if (offset >= chunkStarts(lci) && offset < chunkEnds(lci)) {
                lci
              } else {
                var i = 0
                var done = false
                while (i < chunks.length && !done) {
                  val s = chunkStarts(i)
                  val e = chunkEnds(i)
                  if (offset >= s && offset < e) {
                    done = true
                  } else {
                    i += 1
                  }
                }
                if (i > chunks.length) {
                  sys.error(s"Overflow $i $offset $f")
                }
                i
              }
            }
            lastChunkIdx = chunkIdx
            val chunk = chunks(chunkIdx)
            chunk.addr + (offset - chunk.startOffset)
          }
          override def close(): Unit = chunks.foreach(_.close())
          override val length = fLen
        }
      }
    }
  }

  def makeChunk(f: File, startOffset: Long, sz: Long): Chunk = {
    val chan = FileChannel.open(f.toPath, StandardOpenOption.READ)
    //We cannot open beyond the length of the file
    val actualSz = (f.length() - startOffset).min(sz)
    val mmap: MappedByteBuffer = chan.map(FileChannel.MapMode.READ_ONLY, startOffset, actualSz)
    new Chunk(chan, mmap, startOffset)
  }

  class Chunk(val chan: FileChannel, val mmap: MappedByteBuffer, val startOffset: Long) {
    val addr = mmap.asInstanceOf[sun.nio.ch.DirectBuffer].address
    def close() = chan.close()
  }
}

trait BigMemoryMappedFile {
  def length : Long
  def getAddr(offset: Long): Long
  def close()
}