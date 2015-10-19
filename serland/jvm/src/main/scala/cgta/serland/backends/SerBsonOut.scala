package cgta.serland
package backends

import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint
import com.mongodb.{BasicDBList, BasicDBObject}
import org.bson.types.Binary


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/28/11 12:36 AM
//////////////////////////////////////////////////////////////


object SerBsonOut {
  def toDBObject[A: SerClass](a: A): BasicDBObject = {
    toAny(a) match {
      case a: BasicDBObject => a
      case _ =>
        WRITE_ERROR(s"Cannot encode [$a] as BasicDBObject, use SerMongoOut.toAny for encoding root level primitive values")
    }
  }

  def toAny[A: SerClass](a: A): Any = {
    val bldr = new SerBsonBuilder {}
    try {
      implicitly[SerClass[A]].write(a, new SerBsonOut(bldr))
      bldr.lastO.get.unwrapped
    } catch {
      case e: Throwable => {
        WRITE_ERROR(s"Encoding error: ${bldr.lastO} ${bldr.stack} ${bldr.nextField}", e)
      }
    }
  }
}


private[backends] sealed trait BsonStackElem {def unwrapped: Any}
private[backends] case class BsonList(override val unwrapped: BasicDBList) extends BsonStackElem
private[backends] case class BsonObject(override val unwrapped: BasicDBObject) extends BsonStackElem
private[backends] case class BsonAny(override val unwrapped: Any) extends BsonStackElem

trait SerBsonBuilder {
  var stack    : List[BsonStackElem]   = Nil
  var nextField: Option[String]        = None
  var lastO    : Option[BsonStackElem] = None

  def writeStartObject() {
    val o = new BasicDBObject()
    addIt(o, isNested = true)
    stack ::= BsonObject(o)
  }
  def writeEndObject() {
    popO()
  }
  def writeStartArray() {
    val l = new BasicDBList()
    addIt(l, isNested = true)
    stack ::= BsonList(l)
  }
  def writeEndArray() {
    popO()
  }
  def writeFieldName(name: String) { pushF(name) }
  def writeFieldEnd() { nextField = None }
  def writeString(s: String) = addIt(s)
  def writeBoolean(b: Boolean) = addIt(b: java.lang.Boolean)
  def writeInt(n: Int) = addIt(n: java.lang.Integer)
  def writeLong(n: Long) = addIt(n: java.lang.Long)
  def writeDouble(n: Double) = addIt(n: java.lang.Double)
  def writeByteArray(xs: Array[Byte], offset: Int, len: Int) = {
    val bs = new Array[Byte](len - offset)
    xs.copyToArray(bs, offset, len)
    addIt(new Binary(bs))
  }

  private def addIt(x: AnyRef, isNested: Boolean = false) {
    stack.headOption match {
      case Some(BsonList(xs)) => xs.add(x)
      case Some(BsonObject(o)) => o.put(popF, x)
      case Some(BsonAny(v)) => WRITE_ERROR(s"Cannot add [$x] onto a primitive value [$v]")
      case None => if (!isNested) {
        val wrapped = BsonAny(x)
        lastO = Some(wrapped)
        stack ::= wrapped
      }
    }
  }

  private def popO() {
    lastO = stack.headOption
    stack = stack.tail
  }

  private def pushF(n: String) = {
    if (!nextField.isEmpty) WRITE_ERROR(s"Next field wasn't empty next[$nextField] cur[$n]")
    nextField = Some(n)
  }
  private def popF = {
    val r = nextField.get
    nextField = None
    r
  }

}

/**
 * Copy paste job from SerJson at the moment, monadic nesting is tricky.
 */
class SerBsonOut(g: SerBsonBuilder, override val isHumanReadable: Boolean = true) extends SerOutput {
  var monNestingStack: List[Int] = Nil
  override def writeStructBegin() = {
    g.writeStartObject()
    monNestingStack ::= 0
  }
  override def writeStructEnd() = {
    monNestingStack = monNestingStack.tail
    g.writeEndObject()
  }

  override def writeOneOfBegin(keyName: String, keyId: Int) = {
    g.writeStartObject()
    g.writeFieldName("key")
    g.writeString(keyName)
    g.writeFieldName("value")
  }
  override def writeOneOfEnd() { g.writeEndObject() }

  override def writeFieldBegin(name: String, id: Int) { g.writeFieldName(name) }
  override def writeFieldEnd() { g.writeFieldEnd() }
  override def writeString(s: String) { g.writeString(s) }
  override def writeBoolean(b: Boolean) { g.writeBoolean(b) }
  override def writeChar(c: Char) { g.writeString(c.toString) }
  override def writeByteArrLen(a: Array[Byte], offset: Int, length: Int) { g.writeByteArray(a, offset, length) }
  override def writeByte(a: Byte) { g.writeInt(a) }
  override def writeInt32(a: Int, hint: Ser32Hint) { g.writeInt(a) }
  override def writeInt64(a: Long, hint: Ser64Hint) { g.writeLong(a) }
  override def writeDouble(a: Double) { g.writeDouble(a) }

  override def writeIterable[A](xs: Iterable[A], sca: SerWritable[A]) = {
    g.writeStartArray()
    monNestingStack = monNestingStack match {
      case Nil => 1 :: Nil
      case x :: xs => x + 1 :: xs
    }
    xs.foreach(x => sca.write(x, this))
    monNestingStack = (monNestingStack.head - 1) :: monNestingStack.tail
    g.writeEndArray()
  }
  override def writeOption[A](x: Option[A], sca: SerWritable[A]) = {
    val curDepth = monNestingStack.head
    if (curDepth > 0) g.writeStartArray()
    monNestingStack = (curDepth + 1) :: monNestingStack.tail
    if (x.isDefined) {
      sca.write(x.get, this)
    }
    monNestingStack = curDepth :: monNestingStack.tail
    if (curDepth > 0) g.writeEndArray()
  }
}