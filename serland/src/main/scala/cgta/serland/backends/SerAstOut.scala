package cgta.serland
package backends

import cgta.oscala.util.debugging.PRINT
import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/26/14 9:42 AM
//////////////////////////////////////////////////////////////
import SerAstNodes._

object SerAstOut {

  def toAst[A: SerClass](a: A): SerAstRoot = {
    val encoder = new SerAstOut(isHumanReadable = true)
    serClass[A].write(a, encoder)
    encoder.root
  }

}

class SerAstOut(override val isHumanReadable: Boolean) extends SerOutput {
  val root = SerAstRoot()

  var stack = List[SerAstNode](root)

  def add(leaf: SerAstNode) {
    val h :: _ = stack
    h.add(leaf)
  }

  def pushCon(con: SerAstNode with SerAstCon) {
    add(con)
    stack ::= con
  }
  def popCon() {
    stack = stack.tail
  }

  override def writeFieldBegin(name: String, id: Int) { pushCon(SerAstField(name, id)) }
  override def writeFieldEnd() { popCon() }

  override def writeStructBegin() { pushCon(SerAstStruct()) }
  override def writeStructEnd() { popCon() }
  override def writeOneOfBegin(keyName: String, keyId: Int) { pushCon(SerAstOneOf(keyName, keyId)) }
  override def writeOneOfEnd() { popCon() }


  override def writeOption[A](xs: Option[A], sca: SerWritable[A]) {
    pushCon(SerAstOption())
    xs.foreach(x => sca.write(x, this))
    popCon()
  }
  override def writeIterable[A](xs: Iterable[A], sca: SerWritable[A]) {
    pushCon(SerAstIterable())
    xs.foreach { x =>
      sca.write(x, this)
    }
    popCon()
  }

  override def writeBoolean(b: Boolean) { add(SerAstBoolean(b)) }
  override def writeByte(b: Byte) { add(SerAstByte(b)) }
  override def writeChar(a: Char) { add(SerAstChar(a)) }
  override def writeInt32(a: Int, hint: Ser32Hint) { add(SerAstInt32(a, hint)) }
  override def writeInt64(a: Long, hint: Ser64Hint) { add(SerAstInt64(a, hint)) }
  override def writeDouble(a: Double) { add(SerAstDouble(a)) }
  override def writeString(s: String) { add(SerAstString(s)) }

  override def writeByteArrLen(a: Array[Byte], offset: Int, len: Int) {
    add(SerAstByteArr(a.slice(offset, offset + len).toArray))
  }


}