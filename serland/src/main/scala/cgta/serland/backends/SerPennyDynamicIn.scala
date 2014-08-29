package cgta.serland
package backends

import cgta.oscala.OPlatform

import scala.collection.mutable.ArrayBuffer
import java.io.{ByteArrayInputStream, InputStream}
import scala.annotation.tailrec
import cgta.oscala.util.{Utf8Help, BinaryHelp}
import BinaryHelp.{ByteArrayInStreamReader, InStreamReader}

//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/26/11 5:53 PM
//////////////////////////////////////////////////////////////

object SerPennyDynamicIn {
  trait SPNode {
    def toJson: String
  }
  case class SPStruct(fields: Array[(Int, SPNode)]) extends SPNode {
    override def toJson = fields.map(n_v => '"' + n_v._1.toString + '"' + ":" + n_v._2.toJson).mkString("{", ",", "}")
  }
  case class SPInt(x: Long) extends SPNode {
    override def toJson = x.toString
  }
  case class SPFixed(x: Long) extends SPNode {
    override def toJson = x.toString
  }
  case class SPOneOf(key: Int, sub: SPNode) extends SPNode {
    override def toJson = "{key : %s, value : %s}" format(key.toString, sub.toJson)
  }
  case class SPList(xs: Array[SPNode]) extends SPNode {
    override def toJson = xs.map(_.toJson).mkString("[", ",", "]")
  }
  case class SPByteArray(xs: Array[Byte]) extends SPNode {
    override def toJson = "\"" + Utf8Help.fromBytes(xs) + "\""
  }
  case class SPError(reason: String) extends SPNode {
    override def toJson = "<!" + reason + "!>"
  }


  def fromBytes(barr: Array[Byte]): SPNode = {
    val ins = new ByteArrayInStreamReader(barr)
    new SerPennyDynamicIn(ins).next()
  }
}

class SerPennyDynamicIn(ins: InStreamReader) {
  import SerPennyDynamicIn._
  val I = BinaryHelp.InStream
  def next(): SerPennyDynamicIn.SPNode = {
    //We expect some kind of type to start us off
    dispatchOnWt(I.readUVar(ins).toInt)
  }
  def dispatchOnWt(wt: Int): SerPennyDynamicIn.SPNode = {
    SerPenny.WTs.elemMap.get(wt) match {
      case Some(wt) => wt match {
        case SerPenny.WTs.WStruct => readStruct()
        case SerPenny.WTs.WSVarInt => readVarInt()
        case SerPenny.WTs.WFixed8 => readFixed8()
        case SerPenny.WTs.WFixed64 => readFixed64()
        case x: SerPenny.WTs.WOneOf => readOneOf()
        case x: SerPenny.WTs.WList => readList()
        case x: SerPenny.WTs.WByteArray => readByteArray()
      }
      case None => SPError("BadWireType " + wt)
    }
  }

  def readStruct() = {
    val buf = ArrayBuffer.empty[(Int, SPNode)]
    @tailrec
    def loop() {
      val (fid, wt) = crackFidWT(I.readUVar(ins))
      if (fid == 0) {
      } else {
        buf += ((fid, dispatchOnWt(wt)))
        loop()
      }
    }
    loop()
    SPStruct(buf.toArray)
  }

  def readVarInt() = SPInt(I.readSVar(ins))

  def readFixed8() = SPFixed(I.readByte(ins))

  def readFixed64() = SPFixed(I.readRawLittleEndian64(ins))

  def readOneOf() = {
    val keyId = I.readUVar(ins).toInt
    val wt = I.readUVar(ins).toInt
    SPOneOf(keyId, dispatchOnWt(wt))
  }

  def readList() = {
    val len = I.readUVar(ins).toInt
    if (len == 0) {
      SPList(Array.empty)
    } else {
      val wt = I.readUVar(ins).toInt
      val arr = new Array[SPNode](len)
      def loop(i: Int) {
        if (i < len) {
          arr(i) = dispatchOnWt(wt)
          loop(i + 1)
        }
      }
      loop(0)
      SPList(arr)
    }
  }

  def readByteArray() = {
    val len = I.readUVar(ins).toInt
    SPByteArray(I.readByteArray(len)(ins))
  }

  def crackFidWT(fidWT: Long): (Int, Int) = {
    val wt = (fidWT & 0x7).toInt
    val fid = (fidWT >> 3).toInt
    (fid, wt)
  }
}