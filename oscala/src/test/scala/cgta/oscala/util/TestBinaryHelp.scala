package cgta.oscala
package util

import BinaryHelp.{ByteArrayInStreamReader, InStreamReader, OutStreamWriter, ByteArrayOutStreamWriter, OutStream}
import scala.util.Random
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/26/13 10:28 PM
//////////////////////////////////////////////////////////////


object TestBinaryHelp extends FunSuite {
  val samples = 1000
  val B       = BinaryHelp
  def randomLong = {
    val sign = if (Math.random() >= .5) +1 else -1
    (sign * Math.random() * Long.MaxValue).toLong
  }
  def randomInt = {
    val sign = if (Math.random() >= .5) +1 else -1
    (sign * Math.random() * Int.MaxValue).toInt
  }

  test("trailingZeros") {
    def v(e : Int, n : Long) = Assert.isEquals(e, BinaryHelp.trailingZeros(n), n)

    v(0,0)
    v(0,2)
    v(0,64)
    v(0,1)
    v(1,10)
    v(0,5)
    v(0,15)
    v(0,-15)
    v(1,150)
    v(1,-150)
    v(4,-150000)
  }

  test("popCnt32") {
    Assert.isEquals(0, BinaryHelp.popCnt32(0))
    Assert.isEquals(1, BinaryHelp.popCnt32(1))
    Assert.isEquals(1, BinaryHelp.popCnt32(2))
    Assert.isEquals(8, BinaryHelp.popCnt32(0xFF))
    Assert.isEquals(1, BinaryHelp.popCnt32(Int.MinValue))
    Assert.isEquals(32, BinaryHelp.popCnt32(-1))
    Assert.isEquals(31, BinaryHelp.popCnt32(Int.MaxValue))
  }


  test("ZigZag64") {
    (0 until samples).foreach { dc =>
      val x = randomLong
      Assert.isEquals(x, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(x)))
    }
    Assert.isEquals(0L, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(0)))
    Assert.isEquals(-1L, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(-1)))
    Assert.isEquals(1L, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(1)))
    Assert.isEquals(Long.MaxValue, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(Long.MaxValue)))
    Assert.isEquals(Long.MinValue, BinaryHelp.decodeZigZag64(BinaryHelp.encodeZigZag64(Long.MinValue)))
  }

  test("SwapEndian") {
    Assert.isEquals(0xAABB.toShort, BinaryHelp.swapEndian16(0xBBAA.toShort))
    Assert.isEquals(0xAABBCCDD, BinaryHelp.swapEndian32(0xDDCCBBAA))
    Assert.isEquals(0x1122334455667788L, BinaryHelp.swapEndian64(0x8877665544332211L))
  }

  test("LittleEndian") {
    Assert.isEquals(
      List(1, 2, 3, 4, 5, 6, 7, 8).map(_.toByte),
      BinaryHelp.littleEndian(0x0807060504030201L).toList)
    Assert.isEquals(
      List(1, 2, 3, 4, 5, 6, 7, 0x10.toByte).map(_.toByte),
      BinaryHelp.littleEndian(0x1007060504030201L).toList)
  }

  test("BigEndian") {
    Assert.isEquals(
      List(8, 7, 6, 5, 4, 3, 2, 1).map(_.toByte),
      BinaryHelp.bigEndian(0x0807060504030201L).toList)
    Assert.isEquals(
      List(0x10.toByte, 7, 6, 5, 4, 3, 2, 1).map(_.toByte),
      BinaryHelp.bigEndian(0x1007060504030201L).toList)
  }

  test("Outstream") {
    def validate(expected: List[Byte])(f: OutStreamWriter => Unit) {
      val os = new ByteArrayOutStreamWriter()
      f(os)
      Assert.isEquals(expected.toList, os.toByteArray.toList)
    }

    def doubleCheck[A](x: A)(f: OutStreamWriter => Unit)(g: InStreamReader => A) {
      val baos = new ByteArrayOutStreamWriter()
      f(baos)
      Assert.isEquals(x, g(new ByteArrayInStreamReader(baos.toByteArray)))
    }

    val n = 0xA


    validate(B.bigEndian(B.encodeZigZag64(n)).toList.dropWhile(_ == 0)) { implicit os =>
      OutStream.writeSVar(n)
    }

    validate(B.bigEndian(n).toList.dropWhile(_ == 0)) { implicit os =>
      OutStream.writeUVar(n)
    }

    for (i <- 0 to 255) {
      val b = i.toByte
      doubleCheck(b)(B.OutStream.writeByte(b)(_))(B.InStream.readByte(_))
    }

    doubleCheck(-55566L)(B.OutStream.writeSVar(-55566L)(_))(B.InStream.readSVar(_))
    doubleCheck(55566L)(B.OutStream.writeUVar(55566L)(_))(B.InStream.readUVar(_))
    doubleCheck(-1L)(B.OutStream.writeUVar(-1L)(_))(B.InStream.readUVar(_))
    //      doubleCheck(55566.0)(B.OutStream.writeDouble(55566.0)(_))(B.InStream.readDouble(_))
    doubleCheck(55566.0)(B.OutStream.writeRawLittleEndian32(55566)(_))(B.InStream.readRawLittleEndian32(_))
    doubleCheck(List[Byte](1, 2, 3, 4, 5))(B.OutStream.writeByteArray(Array[Byte](1, 2, 3, 4, 5))(_))(i =>
      B.InStream.readByteArray(5)(i).toList)
    doubleCheck(List[Byte](2, 3))(B.OutStream.writeByteArray(Array[Byte](1, 2, 3, 4, 5), 1, 2)(_))(i =>
      B.InStream.readByteArray(2)(i).toList)

    doubleCheck(List[Byte](2, 3, 4, 5)) { implicit o =>
      B.OutStream.writeSVar(-1L)
      B.OutStream.writeRawLittleEndian64(-1L)
      B.OutStream.writeByteArray(Array[Byte](1, 2, 3, 4, 5))
    } {
      implicit i =>
        B.InStream.skipVar
        B.InStream.skip(8)
        B.InStream.skip(1)
        B.InStream.readByteArray(4).toList
    }
  }

  def longTest(value: Long) {
    val bytes = new Array[Byte](8)
    B.writeToBytesBigEndian64(0, value, bytes)
    val result = B.readFromBytesBigEndian64(0, bytes)
    Assert.isEquals(value, result)
  }


  test("LongSimpleEncode") {
    longTest(0L)
  }

  test("LongExtremes") {
    longTest(Long.MaxValue)
    longTest(Long.MinValue)
  }

  test("LongFuzzy") {
    val random = new Random()
    for (i <- 1 to samples) {
      longTest(random.nextLong())
    }
  }

  def intTest(value: Int) {
    val bytes = new Array[Byte](4)
    B.writeToBytesBigEndian32(0, value, bytes)
    val result = B.readFromBytesBigEndian32(0, bytes)
    Assert.isEquals(value, result)
  }


  test("IntSimpleEncode") {
    intTest(0)
  }

  test("IntExtremes") {
    intTest(Int.MaxValue)
    intTest(Int.MinValue)
  }

  test("IntFuzzy") {
    val random = new Random()
    for (i <- 1 to samples) {
      intTest(random.nextInt())
    }
  }


}