package cgta.serland
package backends

import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint

import scala.collection.mutable.ArrayBuffer


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/26/14 10:26 AM
//////////////////////////////////////////////////////////////

object SerAstNodes {
  sealed trait SerAstNode {
    def dup() : SerAstNode
    def add(c: SerAstNode)
  }
  //Can contain another node
  sealed trait SerAstCon
  //Does not contain another node
  sealed trait SerAstLeaf {self: SerAstNode =>
    override final def add(c: SerAstNode) {WRITE_ERROR(s"CANNOT ADD TO LEAF $c")}
    override final def dup() = this
  }

  case class SerAstRoot(var child: Option[SerAstNode] = None) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      if (child.isDefined) WRITE_ERROR(s"Adding into full root $c $this")
      child = Some(c)
    }
    override def dup(): SerAstNode = copy(child = child.map(_.dup()))
  }

  case class SerAstOneOf(keyName: String, keyId: Int, var child: Option[SerAstNode] = None) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      if (child.isDefined) WRITE_ERROR(s"Adding into full One Of $c $this")
      child = Some(c)
    }
    override def dup(): SerAstNode = copy(child = child.map(_.dup()))
  }
  case class SerAstField(name: String, id: Int, var child: Option[SerAstNode] = None) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      if (child.isDefined) WRITE_ERROR(s"Adding into full Field $c $this")
      child = Some(c)
    }
    override def dup(): SerAstNode = copy(child = child.map(_.dup()))
  }
  case class SerAstStruct(var children: ArrayBuffer[SerAstField] = new ArrayBuffer) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      c match {
        case f: SerAstField => children += f
        case x => WRITE_ERROR(s"Error tried to add a non field as a child of struct $c $this")
      }
    }
    override def dup(): SerAstNode = copy(children = children.map(_.dup().asInstanceOf[SerAstField]))
  }
  case class SerAstOption(var child: Option[SerAstNode] = None) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      if (child.isDefined) WRITE_ERROR(s"Adding into full Option $c $this")
      child = Some(c)
    }
    override def dup(): SerAstNode = SerAstRoot(child = child.map(_.dup()))
  }
  case class SerAstIterable(var children: ArrayBuffer[SerAstNode] = new ArrayBuffer) extends SerAstNode with SerAstCon {
    override def add(c: SerAstNode) {
      children +=  c
    }
    override def dup(): SerAstNode = copy(children = children.map(_.dup()))
  }


  case class SerAstBoolean(v: Boolean) extends SerAstNode with SerAstLeaf
  case class SerAstByte(v: Byte) extends SerAstNode with SerAstLeaf
  case class SerAstChar(v: Char) extends SerAstNode with SerAstLeaf
  case class SerAstInt32(v: Int, hint: Ser32Hint) extends SerAstNode with SerAstLeaf
  case class SerAstInt64(v: Long, hint: Ser64Hint) extends SerAstNode with SerAstLeaf
  case class SerAstDouble(v: Double) extends SerAstNode with SerAstLeaf
  case class SerAstString(v: String) extends SerAstNode with SerAstLeaf
  case class SerAstByteArr(v: Array[Byte]) extends SerAstNode with SerAstLeaf
}