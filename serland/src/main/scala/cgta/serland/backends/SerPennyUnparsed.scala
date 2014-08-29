package cgta.serland
package backends

import scala.annotation.tailrec
import cgta.oscala.util.BinaryHelp
import BinaryHelp.{InStreamReader, ByteArrayOutStreamWriter}

//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 10/13/11 8:16 AM
//////////////////////////////////////////////////////////////

/**
 * This class just slurps an inputstream and sticks it into an outputstream.
 */
class SerPennyUnparsed(val in: InStreamReader, val initWt: Int) {
  val O = BinaryHelp.OutStream
  val I = BinaryHelp.InStream
  implicit val ins = in
  implicit val out = new ByteArrayOutStreamWriter()
  def slurp() {
    O.writeUVar(initWt)
    dispatchOnWt(initWt)
  }
  private def dispatchOnWt(wt: Int) {
    SerPenny.WTs.elemMap.get(wt) match {
      case Some(wt) => wt match {
        case SerPenny.WTs.WStruct => slurpStruct()
        case SerPenny.WTs.WSVarInt => slurpVarInt()
        case SerPenny.WTs.WFixed8 => slurpFixed8()
        case SerPenny.WTs.WFixed64 => slurpFixed64()
        case x: SerPenny.WTs.WOneOf => slurpOneOf()
        case x: SerPenny.WTs.WList => slurpList()
        case x: SerPenny.WTs.WByteArray => slurpByteArray()
      }
      case None => READ_ERROR("BadWireType " + wt)
    }
  }

  def slurpStruct() {
    @tailrec
    def loop() {
      //All reads have a write
      val raw = I.readUVar
      O.writeUVar(raw)
      val (fid, wt) = crackFidWT(raw)
      if (fid == 0) {
        //go until the stop field
      } else {
        dispatchOnWt(wt)
        loop()
      }
    }
    loop()
  }

  def slurpVarInt() {
    O.writeSVar(I.readSVar)
  }

  def slurpFixed8() {
    O.writeByte(I.readByte)
  }

  def slurpFixed64() {
    O.writeRawLittleEndian64(I.readRawLittleEndian64)
  }

  def slurpOneOf() = {
    O.writeUVar(I.readUVar)
    val wt = I.readUVar
    O.writeUVar(wt)
    dispatchOnWt(wt.toInt)
  }

  def slurpList() = {
    val rawLen = I.readUVar(ins)
    O.writeUVar(rawLen)
    val len = rawLen.toInt
    if (len == 0) {
    } else {
      val wt = I.readUVar(ins)
      O.writeUVar(wt)
      def loop(i: Int) {
        if (i < len) {
          dispatchOnWt(wt.toInt)
          loop(i + 1)
        }
      }
      loop(0)
    }
  }

  def slurpByteArray() = {
    val len = I.readUVar
    O.writeUVar(len)
    val bs = I.readByteArray(len.toInt)
    O.writeByteArray(bs)
  }

  def crackFidWT(fidWT: Long): (Int, Int) = {
    val wt = (fidWT & 0x7).toInt
    val fid = (fidWT >> 3).toInt
    (fid, wt)
  }
}