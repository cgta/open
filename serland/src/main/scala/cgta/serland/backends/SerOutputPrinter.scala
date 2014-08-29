package cgta.serland.backends

import cgta.serland._
import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint

//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/28/11 1:50 PM
//////////////////////////////////////////////////////////////


class SerOutputPrinter(override val isHumanReadable: Boolean = true) extends SerOutput {
  private var monStack: List[Int]    = 0 :: Nil
  private var stack   : List[String] = Nil
  private def p(s: String) = print(s)
  private def push(s: String) = stack ::= s
  private def pop(s: () => Unit = null) = {
    if (s != null) {
      print("  " * (stack.size - 1) + q(stack.head) + ":")
      s()
      print("")
    }
    stack = stack.tail
  }
  private def q(string: String) = "\"" + string + "\""
  def writeStructBegin() = {
    p("{")
    monStack ::= 0
  }
  def writeStructEnd() = {
    monStack = monStack.tail
    p("}")
  }
  def writeFieldBegin(name: String, id: Int) = {
    push(name)
  }
  def writeFieldEnd() {
  }
  def writeOneOfBegin(keyName: String, keyId: Int) = {
    writeStructBegin()
    push("key")
    writeString(keyName)
    push("value")
  }
  def writeOneOfEnd() = writeStructEnd()
  def writeIterable[A](xs: Iterable[A], sca: SerWritable[A]) = {
    monStack = if (monStack.isEmpty) {
      List(1)
    } else {
      (monStack.head + 1) :: monStack.tail
    }

    p("[")
    val itr = xs.iterator
    while (itr.hasNext) {
      sca.write(itr.next(), this)
      if (itr.hasNext) p(",")
    }
    p("]")

    monStack = (monStack.head - 1) :: monStack.tail
  }
  def writeOption[A](x: Option[A], sca: SerWritable[A]) = {
    val curDepth = monStack.head
    if (curDepth > 0) p("[")
    monStack = (curDepth + 1) :: monStack.tail
    if (x.isDefined) {
      sca.write(x.get, this)
    }
    monStack = curDepth :: monStack.tail
    if (curDepth > 0) p("]")

  }

  //Primitives
  /**
   * Note that for this method a toString version can be provided
   * this toString version will be used where human readability is
   * important, or the WriteFieldStart is called with the UseString SerHint
   *
   * The actual encoding scheme that backend chooses will depend on the SerHints
   */

  def writeBoolean(b: Boolean) = pop(() => this p b.toString)
  def writeChar(a: Char) = pop(() => this p a.toString)
  def writeByte(a: Byte) = pop(() => this p a.toString)
  def writeInt32(a: Int, hint : Ser32Hint) = pop(() => this p a.toString)
  def writeInt64(a: Long, hint : Ser64Hint) = pop(() => this p a.toString)
  def writeDouble(a: Double) = pop(() => this p a.toString)
  def writeString(s: String) = pop(() => this p q(s))
  def writeByteArrLen(a: Array[Byte], offset: Int, length: Int) =
    pop(() => this p a.drop(offset).take(length).map(_.toString).mkString("bin[", ",", "]"))

}

