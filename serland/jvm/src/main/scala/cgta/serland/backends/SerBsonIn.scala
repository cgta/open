package cgta.serland
package backends

import com.mongodb.DBObject
import scala.collection.JavaConverters
import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/28/11 2:38 AM
//////////////////////////////////////////////////////////////

import JavaConverters._

object SerBsonIn {
  def fromDBObject[A: SerClass](obj: DBObject): A = fromAny(obj)
  def fromAny[A: SerClass](a: Any): A = {
    implicitly[SerClass[A]].read(new SerBsonIn(SerBsonNode.tryWrap(a).getOrElse(READ_ERROR("Unable to wrap any" + a))))
  }
}

class SerBsonIn(root: SerBsonNode) extends SerInput {
  final override val isHumanReadable: Boolean = true

  private val lastFieldUnknown = -1
  private val lastFieldMissing = 0
  private val lastFieldPresent = 1

  private var stack                           = List(root)
  private var monStack       : List[Int]      = List(0)
  private var lastField      : Option[String] = None
  private var lastFieldStatus: Int            = lastFieldUnknown

  private def badType(n: SerBsonNode, reason: String = ""): Nothing = {
    READ_ERROR(reason + " Unexpected type encountered: " + n + "Stacks " + stack + " " + monStack + " " + lastField)
  }
  private def pop = {
    val h = stack.head
    stack = stack.tail
    h
  }

  override def readByteArr(): Array[Byte] = pop match {
    case SerBsonBinary(bs) => bs.getData
    case n => badType(n)
  }

  override def readString(): String = pop match {
    case SerBsonString(s) => s
    case n => badType(n)
  }

  override def readByte(): Byte = pop match {
    case SerBsonInt(i) => i.toByte
    case n => badType(n)
  }
  override def readInt32(hint : Ser32Hint): Int = pop match {
    case SerBsonInt(i) => i
    case n => badType(n)
  }

  override def readInt64(hint : Ser64Hint): Long = pop match {
    case SerBsonLong(n) => n
    case n => badType(n)
  }
  override def readDouble(): Double = pop match {
    case SerBsonDouble(n) => n
    case n => badType(n)
  }

  override def readBoolean(): Boolean = pop match {
    case SerBsonBoolean(b) => b
    case n => badType(n)
  }

  override def readChar(): Char = pop match {
    case n@SerBsonString(s) =>
      if (s.size == 1) {
        s.head
      } else {
        badType(n, s"String length must be exactly 1 [$s]")
      }
    case n => badType(n)
  }

  override def readIterable[A](sca: SerReadable[A]): Iterable[A] = {
    pop match {
      case ns@SerBsonList(xs) =>
        monStack = (monStack.head + 1) :: monStack.tail
        val res = xs.iterator.asScala.map { x =>
          SerBsonNode.tryWrap(x) match {
            case Some(n) =>
              stack ::= n
              val r = sca.read(this)
              r
            case _ => badType(ns, "Cannot parse this " + x)
          }
        }.toIndexedSeq
        monStack = (monStack.head - 1) :: monStack.tail
        res
      case n => badType(n)
    }
  }

  override def readOption[A](sca: SerReadable[A]): Option[A] = {
    val curDepth = monStack.head
    monStack = (curDepth + 1) :: monStack.tail
    val res = if (curDepth > 0) {
      pop match {
        case ns@SerBsonList(xs) =>
          xs.iterator.asScala.toList match {
            case Nil => None
            case x :: Nil =>
              SerBsonNode.tryWrap(x) match {
                case Some(n) =>
                  stack ::= n
                  Some(sca.read(this))
                case _ => badType(ns, "Inside of option, cannot parse this" + x)
              }
            case xs => badType(ns, "Nested Options should only have 0 or 1 element, had " + xs.size)
          }
        case n => badType(n)
      }
    } else {
      val lfs = lastFieldStatus
      lastFieldStatus = lastFieldUnknown

      if (lfs == lastFieldPresent) {
        Some(sca.read(this))
      } else if (lfs == lastFieldMissing) {
        None
      } else {
        READ_ERROR("Unset options should be called after a readFieldBegin")
      }
    }
    monStack = curDepth :: monStack.tail
    res
  }

  override def readOneOfBegin(): Either[String, Int] = {
    readStructBegin()
    readFieldBegin("k", 1)
    val key = if (lastFieldStatus == lastFieldPresent) {
      val k = readString()
      readFieldEnd()
      k
    } else {
      readFieldEnd()
      readFieldBegin("key", 1)
      if (lastFieldStatus == lastFieldPresent) {
        val k = readString()
        readFieldEnd()
        k
      } else {
        READ_ERROR("Cannot find k/key field for a OneOf")
      }
    }
    readFieldBegin("value", 2)
    if (lastFieldStatus == lastFieldPresent) {
      Left(key)
    } else {
      readFieldEnd()
      readFieldBegin("v", 2)
      if (lastFieldStatus == lastFieldPresent) {
        Left(key)
      } else {
        READ_ERROR("Cannot find v/value field for a OneOf")
      }
    }
  }
  override def readOneOfEnd() = {
    readFieldEnd()
    readStructEnd()
  }
  override def readFieldBegin(name: String, id: Int) {
    lastField = Some(name)
    stack.head match {
      case n@SerBsonObj(o) =>
        if (o.containsField(name)) {
          lastFieldStatus = lastFieldPresent
          val x = o.get(name)
          SerBsonNode.tryWrap(x) match {
            case Some(n) =>
              stack ::= n
            case _ => badType(n, "Cannot parse this " + x)
          }
        } else {
          lastFieldStatus = lastFieldMissing
        }
      case n => badType(n)
    }
  }

  override def readFieldEnd() {
    lastFieldStatus = lastFieldUnknown
  }
  override def readStructBegin() = {
    stack.head match {
      case SerBsonObj(o) => monStack ::= 0
      case n => badType(n)
    }
  }
  override def readStructEnd() = {
    stack = stack.tail
    monStack = monStack.tail
  }

  override def readSerInput() = {
    val popped = pop
    () => new SerBsonIn(popped)
  }
}


