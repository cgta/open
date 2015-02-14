package cgta.serland
package backends

import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint
import cgta.serland.backends.SerJsonIn.{LastFieldStatus, LastFieldPresent, LastFieldMissing, LastFieldUnknown}
import cgta.serland.json.{JsonIO, JsonNodes}

//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 1/7/11 4:22 PM
//////////////////////////////////////////////////////////////

object SerJsonIn {
  def fromJsonString[A: SerClass](s: String, d: JsonDerefer = null): A = {
    val ser = implicitly[SerClass[A]]
    try {
      ser.read(new SerJsonIn(JsonIO.read(s), derefer = d.nullSafe))
    } catch {
      case e: Throwable =>
        READ_ERROR(s"Unable to parse [$s]", e)
    }
  }

  def apply(s: String): SerJsonIn = {
    new SerJsonIn(JsonIO.read(s))
  }

  sealed trait LastFieldStatus {
    def missing = false
    def present = false
    def name: String
  }
  case class LastFieldMissing(name: String) extends LastFieldStatus {
    override val missing = true
  }
  case class LastFieldPresent(name: String) extends LastFieldStatus {
    override val present = true
  }
  case class LastFieldUnknown() extends LastFieldStatus {
    def name = "Unknown"
  }
}


/**
 * This class with read in values from jackson root nodes.
 *
 * derefer, a special trait that is used when we are deserializing
 * json data that might contain refs. This will allow proper reconstruction
 * of the data.
 *
 */
class SerJsonIn(root: JsonNodes.Value, derefer: Option[JsonDerefer] = None) extends SerInput {


  final override val isHumanReadable: Boolean               = true
  private        var stack          : List[JsonNodes.Value] = List(root)
  private        var monStack       : List[Int]             = List(0)

  private var lastFieldStatus: LastFieldStatus = LastFieldUnknown()

  private def push(v: JsonNodes.Value) {
    stack ::= v
  }
  private def fieldCheck() {
    if (lastFieldStatus.missing) {READ_ERROR(s"Missing Required Field: ${lastFieldStatus.name}")}
  }
  private def pop() = {
    fieldCheck()
    val h = stack.head
    stack = stack.tail
    h
  }
  override def readByteArr(): Array[Byte] = {
    val node = pop()
    if (node.isArray) {
      node.getElementsItr.map(n => n.getIntValue.toByte).toArray
    } else {
      READ_ERROR("Unexpected node in json")
    }
  }

  override def readString(): String = pop().getTextValue
  override def readByte(): Byte = pop().getIntValue.toByte
  override def readInt32(hint: Ser32Hint): Int = pop().getIntValue
  override def readInt64(hint: Ser64Hint): Long = {
    val n = pop()
    if (n.isNumber) {
      n.getLongValue
    } else {
      n.getTextValue.toLong
    }
  }
  override def readDouble(): Double = pop().getDoubleValue
  override def readBoolean(): Boolean = pop().getBooleanValue
  override def readChar(): Char = pop().getTextValue.head
  override def readIterable[A](sca: SerReadable[A]): Iterable[A] = {
    val xs = pop()
    if (!xs.isArray) READ_ERROR("Expected xs to be an array")
    monStack = (monStack.head + 1) :: monStack.tail
    val res = xs.getElementsItr.map {
      e =>
        push(e)
        val r = sca.read(this)
        r
    }.toIndexedSeq
    monStack = (monStack.head - 1) :: monStack.tail
    res
  }
  override def readOption[A](sca: SerReadable[A]): Option[A] = {
    val curDepth = monStack.head
    monStack = (curDepth + 1) :: monStack.tail
    val res = if (curDepth > 0) {
      val h = pop()
      if (!h.isArray) READ_ERROR("When reading an option of depth, expected h to be an array")
      val xs = h.getElementsSeq
      xs.size match {
        case 0 => None
        case 1 =>
          stack ::= xs.head
          Some(sca.read(this))
        case sz => READ_ERROR("Nested Options should only have 0 or 1 element, had " + sz)
      }
    } else {
      val lfs = lastFieldStatus
      lastFieldStatus = LastFieldUnknown()
      if (lfs.present) {
        Some(sca.read(this))
      } else if (lfs.missing) {
        None
      } else {
        Some(sca.read(this))
      }
    }
    monStack = curDepth :: monStack.tail
    res
  }
  override def readOneOfBegin(): Either[String, Int] = {
    readStructBegin()
    readFieldBegin("k", 1)
    val key = if (lastFieldStatus.present) {
      val k = readString()
      readFieldEnd()
      k
    } else {
      readFieldEnd()
      readFieldBegin("key", 1)
      if (lastFieldStatus.present) {
        val k = readString()
        readFieldEnd()
        k
      } else {
        READ_ERROR("Unable to find the k/key field for a OneOf")
      }
    }
    stack.head.get("v") match {
      case None =>
        stack.head.get("value") match {
          case None =>
            READ_ERROR("Unable to find the v/value field for a OneOf")
          case Some(v) =>
            push(v)
        }
      case Some(v) =>
        push(v)
    }
    Left(key)
  }
  override def readOneOfEnd() = readStructEnd()
  override def readFieldBegin(name: String, id: Int) {
    stack.head.get(name) match {
      case None =>
        lastFieldStatus = LastFieldMissing(name)
      case Some(n) =>
        lastFieldStatus = LastFieldPresent(name)
        push(n)
    }
  }

  override def readFieldEnd() {
    lastFieldStatus = LastFieldUnknown()
  }
  override def readStructBegin() = {
    if (!stack.head.isObject) READ_ERROR("Expected stack.head to be an object but was: " + stack.head.toString)
    fieldCheck()

    def extractPathIfRef: Option[String] = {
      //Ensure that there is only field and that that fields name is $ref
      val itr = stack.head.getFieldNames
      def isRef = {
        if (itr.hasNext) {
          itr.next()
          !itr.hasNext
        } else {
          false
        }
      }
      if (isRef) {
        stack.head.get("$ref").collect { case n if n.isTextual => n.getTextValue}
      } else {
        None
      }
    }

    if (derefer.isDefined) {
      extractPathIfRef.foreach { path =>
        //We need to replace the top object on the stack with
        //the new json node we are going to use instead!
        stack = derefer.get.getNode(path) :: stack.tail
      }
    }
    monStack ::= 0
  }
  override def readStructEnd() = {
    pop()
    monStack = monStack.tail
  }
  override def readSerInput() = {
    val popped = pop()
    () => new SerJsonIn(popped)
  }

}