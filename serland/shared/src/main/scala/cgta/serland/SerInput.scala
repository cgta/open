package cgta.serland

import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint

//////////////////////////////////////////////////////////////
// Copyright (c) 2010 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 12/23/10 8:03 AM
//////////////////////////////////////////////////////////////

trait SerInput {
  //When this is true then fields should be read in in their human readable versions.
  //Some types can write values out as strings or other types depending on if the
  //data needs to be human readable or not. Enumerations are an example of this.
  def isHumanReadable(): Boolean
  def readStructBegin()
  def readStructEnd()
  def readFieldBegin(name: String, id: Int)
  def readFieldEnd()
  def readOneOfBegin(): Either[String, Int]
  def readOneOfEnd()
  def readIterable[A](sca: SerReadable[A]): Iterable[A]
  def readOption[A](sca: SerReadable[A]): Option[A]
  def readBoolean(): Boolean
  def readChar(): Char
  def readByte() : Byte
  def readInt32(hint: Ser32Hint): Int
  def readInt64(hint: Ser64Hint): Long
  def readDouble(): Double
  def readString(): String
  def readByteArr(): Array[Byte]
  def readSerInput(): () => SerInput
}

