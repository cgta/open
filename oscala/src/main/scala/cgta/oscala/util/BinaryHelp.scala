package cgta.oscala
package util

import java.io.InputStream


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/29/14 11:27 AM
//////////////////////////////////////////////////////////////

object BinaryHelp {

  //http://stackoverflow.com/questions/1174505/counting-trailing-zeros-of-numbers-resulted-from-factorial
  def trailingZeros(n: Long): Int = {
    val isEven = (n.toInt & 1L) == 0
    var m = n
    var c = 0
    if (isEven) {
      while (m != 0 && m == (m / 10) * 10) {
        c += 1
        m /= 10
      }
      c
    } else {
      0
    }
  }

  def popCnt32(n: Int): Int = {
    var x = n
    //http://aggregate.org/MAGIC/#Population Count (Ones Count)
    //    32-bit recursive reduction using SWAR...
    //    but first step is mapping 2-bit values
    //    into sum of 2 1-bit values in sneaky way
    x -= ((x >> 1) & 0x55555555)
    x = ((x >> 2) & 0x33333333) + (x & 0x33333333)
    x = ((x >> 4) + x) & 0x0f0f0f0f
    x += (x >> 8)
    x += (x >> 16)
    x & 0x0000003f
  }


  /**
   * Encode a ZigZag-encoded 64-bit value.  ZigZag encodes signed integers
   * into values that can be efficiently encoded with varint.  (Otherwise,
   * negative values must be sign-extended to 64 bits to be varint encoded,
   * thus always taking 10 bytes on the wire.)
   *
   * @param n A signed 64-bit integer.
   * @return An unsigned 64-bit integer, stored in a signed int because
   *         Java has no explicit unsigned support.
   */
  def encodeZigZag64(n: Long): Long = (n << 1) ^ (n >> 63)

  /**
   * Decode a ZigZag-encoded 64-bit value.  ZigZag encodes signed integers
   * into values that can be efficiently encoded with varint.  (Otherwise,
   * negative values must be sign-extended to 64 bits to be varint encoded,
   * thus always taking 10 bytes on the wire.)
   *
   * @param n An unsigned 64-bit integer, stored in a signed int because
   *          Java has no explicit unsigned support.
   * @return A signed 64-bit integer.
   */
  def decodeZigZag64(n: Long): Long = (n >>> 1) ^ -(n & 1)


  def swapEndian16(value: Short): Short = {
    val b1 = value & 0xff
    val b2 = (value >> 8) & 0xff

    (b1 << 8 | b2 << 0).toShort
  }


  def swapEndian32(value: Int): Int = {
    val b1 = (value >> 0) & 0xff
    val b2 = (value >> 8) & 0xff
    val b3 = (value >> 16) & 0xff
    val b4 = (value >> 24) & 0xff

    b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0
  }

  def swapEndian64(value: Long): Long = {
    val b1 = (value >> 0) & 0xff
    val b2 = (value >> 8) & 0xff
    val b3 = (value >> 16) & 0xff
    val b4 = (value >> 24) & 0xff
    val b5 = (value >> 32) & 0xff
    val b6 = (value >> 40) & 0xff
    val b7 = (value >> 48) & 0xff
    val b8 = (value >> 56) & 0xff

    b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8 << 0
  }


  def littleEndian(value: Long): Array[Byte] = {
    val xs = new Array[Byte](8)
    xs(0) = ((value >> 0) & 0xff).toByte
    xs(1) = ((value >> 8) & 0xff).toByte
    xs(2) = ((value >> 16) & 0xff).toByte
    xs(3) = ((value >> 24) & 0xff).toByte
    xs(4) = ((value >> 32) & 0xff).toByte
    xs(5) = ((value >> 40) & 0xff).toByte
    xs(6) = ((value >> 48) & 0xff).toByte
    xs(7) = ((value >> 56) & 0xff).toByte
    xs
  }

  def bigEndian(value: Long): Array[Byte] = {
    val xs = new Array[Byte](8)
    xs(0) = ((value >> 56) & 0xff).toByte
    xs(1) = ((value >> 48) & 0xff).toByte
    xs(2) = ((value >> 40) & 0xff).toByte
    xs(3) = ((value >> 32) & 0xff).toByte
    xs(4) = ((value >> 24) & 0xff).toByte
    xs(5) = ((value >> 16) & 0xff).toByte
    xs(6) = ((value >> 8) & 0xff).toByte
    xs(7) = ((value >> 0) & 0xff).toByte
    xs
  }

  /** Writes a big-endian 32-bit integer. */
  def writeToBytesBigEndian32(startIndex: Int, value: Int, data: Array[Byte]): Unit = {
    data(startIndex) = ((value >>> 24) & 0xFF).toByte
    data(startIndex + 1) = ((value >>> 16) & 0xFF).toByte
    data(startIndex + 2) = ((value >>> 8) & 0xFF).toByte
    data(startIndex + 3) = ((value >>> 0) & 0xFF).toByte
  }

  /** Write a big-endian 64-bit integr (aka Long) */
  def writeToBytesBigEndian64(startIndex: Int, value: Long, data: Array[Byte]): Unit = {
    data(startIndex) = (value >>> 56).toByte
    data(startIndex + 1) = (value >>> 48).toByte
    data(startIndex + 2) = (value >>> 40).toByte
    data(startIndex + 3) = (value >>> 32).toByte
    data(startIndex + 4) = (value >>> 24).toByte
    data(startIndex + 5) = (value >>> 16).toByte
    data(startIndex + 6) = (value >>> 8).toByte
    data(startIndex + 7) = (value >>> 0).toByte
  }

  /** Reads a big-endian 64-bit integer (aka long) */
  def readFromBytesBigEndian64(startIndex: Int, data: Array[Byte]): Long = {
    (data(startIndex).asInstanceOf[Long] << 56) +
      ((data(startIndex + 1) & 255).asInstanceOf[Long] << 48) +
      ((data(startIndex + 2) & 255).asInstanceOf[Long] << 40) +
      ((data(startIndex + 3) & 255).asInstanceOf[Long] << 32) +
      ((data(startIndex + 4) & 255).asInstanceOf[Long] << 24) +
      ((data(startIndex + 5) & 255) << 16) +
      ((data(startIndex + 6) & 255) << 8) +
      ((data(startIndex + 7) & 255) << 0)
  }

  /** Reads a big-endian 32-bit integer. */
  def readFromBytesBigEndian32(startIndex: Int, data: Array[Byte]): Int = {
    (data(startIndex) << 24) +
      ((data(startIndex + 1) & 0xFF) << 16) +
      ((data(startIndex + 2) & 0xFF) << 8) +
      ((data(startIndex + 3) & 0xFF) << 0)
  }


  trait OutStreamWriter {
    //Writes only the lowest 8bits of the int.
    def write(b: Int)
    def write(bs: Array[Byte], offset: Int, len: Int)
  }

  class ByteArrayOutStreamWriter(init: Int = 32) extends OutStreamWriter {
    def toByteArray: Array[Byte] = {
      val res = new Array[Byte](count)
      Array.copy(buf, 0, res, 0, count)
      res
    }

    private var buf   = new Array[Byte](init.max(8))
    private var count = 0
    /**
     * Increases the capacity if necessary to ensure that it can hold
     * at least the number of elements specified by the minimum
     * capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     * @throws OutOfMemoryError if { @code minCapacity < 0}.  This is
     *                          interpreted as a request for the unsatisfiably large capacity
     *                          { @code (long) Integer.MAX_VALUE + (minCapacity - Integer.MAX_VALUE)}.
     */
    private def ensureCapacity(minCapacity: Int) {
      if (minCapacity - buf.length > 0) grow(minCapacity)
    }
    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private def grow(minCapacity: Int) {
      val oldCapacity: Int = buf.length
      var newCapacity: Int = oldCapacity << 1
      if (newCapacity - minCapacity < 0) newCapacity = minCapacity
      if (newCapacity < 0) {
        if (minCapacity < 0) throw new OutOfMemoryError
        newCapacity = Integer.MAX_VALUE
      }
      val newBuf = new Array[Byte](newCapacity)
      Array.copy(buf, 0, newBuf, 0, count)
      buf = newBuf
    }

    override def write(bs: Array[Byte], offset: Int, len: Int): Unit = {
      ensureCapacity(count + len)
      Array.copy(bs, offset, buf, count, len)
      count += len
    }
    override def write(b: Int): Unit = {
      ensureCapacity(count + 1)
      buf(count) = b.asInstanceOf[Byte]
      count += 1
    }


  }

  object OutStream {
    def writeByte(b: Byte)(implicit os: OutStreamWriter) {
      os.write(b)
    }

    //Does zig zag
    def writeSVar(x: Long)(implicit os: OutStreamWriter) {
      writeUVar(encodeZigZag64(x))
    }

    //Does not do zig zag
    def writeUVar(x: Long)(implicit os: OutStreamWriter) {
      var value = x
      while (true) {
        if ((value & ~0x7FL) == 0) {
          os.write(value.asInstanceOf[Int])
          return
        }
        else {
          os.write((value.asInstanceOf[Int] & 0x7F) | 0x80)
          value >>>= 7
        }
      }
    }

    //  import java.lang.{Double => JavaDouble}
    //    def writeDouble(x: Double)(implicit os: OutStreamWriter) {
    //      writeRawLittleEndian64(JavaDouble.doubleToRawLongBits(x))
    //    }

    /** Write a little-endian 32-bit integer. */
    def writeRawLittleEndian32(value: Long)(implicit os: OutStreamWriter): Unit = {
      os.write(value.asInstanceOf[Int] & 0xFF)
      os.write((value >> 8).asInstanceOf[Int] & 0xFF)
      os.write((value >> 16).asInstanceOf[Int] & 0xFF)
      os.write((value >> 24).asInstanceOf[Int] & 0xFF)
    }


    /** Write a little-endian 64-bit integer. */
    def writeRawLittleEndian64(value: Long)(implicit os: OutStreamWriter): Unit = {
      os.write(value.asInstanceOf[Int] & 0xFF)
      os.write((value >> 8).asInstanceOf[Int] & 0xFF)
      os.write((value >> 16).asInstanceOf[Int] & 0xFF)
      os.write((value >> 24).asInstanceOf[Int] & 0xFF)
      os.write((value >> 32).asInstanceOf[Int] & 0xFF)
      os.write((value >> 40).asInstanceOf[Int] & 0xFF)
      os.write((value >> 48).asInstanceOf[Int] & 0xFF)
      os.write((value >> 56).asInstanceOf[Int] & 0xFF)
    }

    def writeByteArray(xs: Array[Byte], offset: Int, len: Int)(implicit os: OutStreamWriter) {
      os.write(xs, offset, len)
    }

    def writeByteArray(xs: Array[Byte])(implicit os: OutStreamWriter) {
      writeByteArray(xs, 0, xs.length)(os)
    }
  }

  object InStreamReader {
    def apply(is: InputStream) = new InStreamReader {
      override def read(): Int = is.read()
      override def read(xs: Array[Byte], offset: Int, len: Int): Int = is.read(xs, offset, len)
    }
  }

  trait InStreamReader {
    def read(): Int
    def read(xs: Array[Byte], offset: Int, len: Int): Int
  }

  class ByteArrayInStreamReader(
    val buf: Array[Byte],
    start: Int = 0,
    length: Int = -1) extends InStreamReader {
    var pos   = start
    val count = if (length < 0) buf.length else length

    override def read(xs: Array[Byte], off: Int, len: Int): Int = {
      if (off < 0 || len < 0 || len > xs.length - off) {
        throw new IndexOutOfBoundsException
      }

      if (pos >= count) {
        return -1
      }

      val avail: Int = count - pos

      val len2 = if (len > avail) {
        avail
      } else {
        len
      }
      if (len2 <= 0) {
        return 0
      }
      System.arraycopy(buf, pos, xs, off, len2)
      pos += len2
      len2
    }
    override def read(): Int = {
      if (pos < count) {
        val res = buf(pos)
        pos += 1
        res & 0xFF
      } else {
        -1
      }
    }
  }

  object InStream {
    def wrap(bs: Array[Byte]) = new ByteArrayInStreamReader(bs)
    def readByte(implicit ins: InStreamReader): Byte = {
      val x = ins.read()
      if (x < 0) sys.error("EOF") else x.toByte
    }
    def readSVar(implicit ins: InStreamReader): Long = {
      decodeZigZag64(readUVar(ins))
    }

    def readUVar(implicit ins: InStreamReader): Long = {
      /** Read a raw Varint from the stream. */
      var shift: Int = 0
      var result: Long = 0
      while (shift < 64) {
        val b: Byte = readByte(ins)
        result |= (b & 0x7F).asInstanceOf[Long] << shift
        if ((b & 0x80) == 0) {
          return result
        }
        shift += 7
      }
      sys.error("Malformed Varint")
    }

    //    def readDouble(implicit ins: InStreamReader): Double = {
    //      JavaDouble.longBitsToDouble(readRawLittleEndian64(ins))
    //    }

    /** Read a little-endian 64-bit integer. */
    def readRawLittleEndian64(implicit ins: InStreamReader): Long = {
      val b1: Byte = readByte(ins)
      val b2: Byte = readByte(ins)
      val b3: Byte = readByte(ins)
      val b4: Byte = readByte(ins)
      val b5: Byte = readByte(ins)
      val b6: Byte = readByte(ins)
      val b7: Byte = readByte(ins)
      val b8: Byte = readByte(ins)
      (b1.asInstanceOf[Long] & 0xff) |
        ((b2.asInstanceOf[Long] & 0xff) << 8) |
        ((b3.asInstanceOf[Long] & 0xff) << 16) |
        ((b4.asInstanceOf[Long] & 0xff) << 24) |
        ((b5.asInstanceOf[Long] & 0xff) << 32) |
        ((b6.asInstanceOf[Long] & 0xff) << 40) |
        ((b7.asInstanceOf[Long] & 0xff) << 48) |
        ((b8.asInstanceOf[Long] & 0xff) << 56)
    }

    /** Read a little-endian 32-bit integer. */
    def readRawLittleEndian32(implicit ins: InStreamReader): Int = {
      val b1: Byte = readByte(ins)
      val b2: Byte = readByte(ins)
      val b3: Byte = readByte(ins)
      val b4: Byte = readByte(ins)
      (b1.asInstanceOf[Int] & 0xff) |
        ((b2.asInstanceOf[Int] & 0xff) << 8) |
        ((b3.asInstanceOf[Int] & 0xff) << 16) |
        ((b4.asInstanceOf[Int] & 0xff) << 24)
    }


    def readByteArray(len: Int)(implicit ins: InStreamReader): Array[Byte] = {
      val arr = new Array[Byte](len)
      var rem = len
      var offset = 0
      while (rem > 0) {
        val amount = ins.read(arr, offset, rem)
        if (amount == -1) {
          sys.error("Stream closed before the expected number of bytes could be read")
        } else {
          rem -= amount
          offset += amount
        }
      }
      arr
    }

    def skip(len: Int)(implicit ins: InStreamReader) {
      var i = 0
      while (i < len) {
        readByte
        i += 1
      }
    }
    def skipVar(implicit ins: InStreamReader) {
      val i = ins.read()
      if (i < 0) {
        sys.error("EOF")
      } else if ((i & 0x80) != 0) {
        skipVar
      }
    }
  }


  object DeltaArray {

    //    object Size {
    //      implicit def fromInt(x : Int) = Size(x)
    //    }
    case class Size(v: Int)
    def writeLongs(arr: Seq[Long])(implicit os: OutStreamWriter, s: Size) = {
      var p = 0L
      var i = 0
      while (i < s.v) {
        val c = arr(i)
        val delta = c - p
        OutStream.writeSVar(delta)
        p = c
        i += 1
      }
    }
    def writeInts(arr: Seq[Int])(implicit os: OutStreamWriter, s: Size) = {
      var p = 0
      var i = 0
      while (i < s.v) {
        val c = arr(i)
        val delta = c - p
        OutStream.writeSVar(delta)
        p = c
        i += 1
      }
    }
    def readLongs(implicit is: InStreamReader, s: Size): Array[Long] = {
      val ret = new Array[Long](s.v)
      var i = 0
      var p = 0L
      while (i < s.v) {
        val delta = InStream.readSVar
        val c = p + delta
        ret(i) = c
        p = c
        i += 1
      }
      ret
    }
    def readInts(implicit is: InStreamReader, s: Size): Array[Int] = {
      val ret = new Array[Int](s.v)
      var i = 0
      var p = 0
      while (i < s.v) {
        val delta = InStream.readSVar.toInt
        val c = p + delta
        ret(i) = c
        p = c
        i += 1
      }
      ret
    }
  }


}