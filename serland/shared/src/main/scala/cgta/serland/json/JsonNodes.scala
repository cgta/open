package cgta.serland
package json


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 12/8/14 4:21 PM
//////////////////////////////////////////////////////////////

object JsonNodes {
  //Legacy
  type JField = (String, Value)
  object JField {
    def apply(s: String, v: Value): JField = s -> v
  }

  sealed trait Value {
    private def BAD_TYPE(tpe: String) = sys.error(s"Cannot call this method when type is not $tpe value [$this]")

    def getBooleanValue: Boolean = BAD_TYPE("Boolean")

    def isNumber = false
    def getIntValue: Int = BAD_TYPE("Number")
    def getLongValue: Long = BAD_TYPE("Number")
    def getDoubleValue: Double = BAD_TYPE("Number")

    def isArray: Boolean = false
    def getElementsItr: Iterator[Value] = BAD_TYPE("Array")
    def getElementsSeq: Seq[Value] = BAD_TYPE("Array")

    def getTextValue: String = BAD_TYPE("String")
    def isTextual = false

    def isObject: Boolean = false
    def get(s: String): Option[Value] = BAD_TYPE("Object")
    def getFieldNames: Iterator[String] = Iterator.empty

    def value: Any
    def apply(i: Int): Value = this.asInstanceOf[Arr].value(i)
    def apply(s: String): Value = get(s).get

    def compact = JsonIO.writeCompact(this)
    def pretty = JsonIO.writePretty(this)
  }
  case class Str(value: String) extends Value {
    override def getTextValue: String = value
    override def isTextual = true
  }
  case class Obj(value: Seq[(String, Value)]) extends Value {
    override def getFieldNames: Iterator[String] = value.iterator.map(_._1)
    override def isObject = true
    override def get(s: String): Option[Value] = {
      value.find(_._1 == s).map(_._2)
    }
    def addField(f : (String, Value)) : Obj = Obj(value :+ f)
  }
  case class Arr(value: Seq[Value]) extends Value {
    override def isArray = true
    override def getElementsItr: Iterator[Value] = value.iterator
    override def getElementsSeq: Seq[Value] = value
  }
  case class Number(value: String) extends Value {
    override def isNumber = true
    override def getIntValue: Int = value.toInt
    override def getLongValue: Long = value.toLong
    override def getDoubleValue: Double = value.toDouble
  }
//  case object False extends Value {
//    override def getBooleanValue: Boolean = false
//    def value = true
//  }
//  case object True extends Value {
//    override def getBooleanValue: Boolean = true
//    def value = false
//  }

  case class Bool(value : Boolean) extends Value {
    override def getBooleanValue: Boolean = value
  }
  case object Null extends Value {
    def value = null
  }
}
