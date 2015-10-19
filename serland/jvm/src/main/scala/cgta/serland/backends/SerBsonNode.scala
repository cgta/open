package cgta.serland
package backends

import com.mongodb.{BasicDBList, BasicDBObject}
import org.bson.types.Binary


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/28/11 3:34 PM
//////////////////////////////////////////////////////////////


object SerBsonNode {
  def tryWrap(x: Any): Option[SerBsonNode] = {
    x match {
      case x: BasicDBObject => Some(SerBsonObj(x))
      case x: BasicDBList => Some(SerBsonList(x))
      case x: String => Some(SerBsonString(x))
      case x: Boolean => Some(SerBsonBoolean(x))
      case x: Int => Some(SerBsonInt(x))
      case x: Long => Some(SerBsonLong(x))
      case x: Double => Some(SerBsonDouble(x))
      case x: Binary => Some(SerBsonBinary(x))
      case x: Array[Byte] => Some(SerBsonBinary(new Binary(x)))
      case x => None
    }
  }
}

sealed trait SerBsonNode
case class SerBsonObj(v: BasicDBObject) extends SerBsonNode
case class SerBsonList(v: BasicDBList) extends SerBsonNode
case class SerBsonString(v: String) extends SerBsonNode
case class SerBsonBoolean(v: Boolean) extends SerBsonNode
case class SerBsonInt(v: Int) extends SerBsonNode
case class SerBsonLong(v: Long) extends SerBsonNode
case class SerBsonDouble(v: Double) extends SerBsonNode
case class SerBsonBinary(v: Binary) extends SerBsonNode


