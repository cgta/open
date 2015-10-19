package cgta.serland

import cgta.serland.gen.Gen


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 10/13/11 8:56 AM
//////////////////////////////////////////////////////////////

/**
 * This ser class is WRITE-ONLY (we need to use the type the system
 * to encode this somehow)
 *
 * It allows for dynamically setting the value that will we be encoded.
 *
 */
object SerForSerOutput {

  implicit val serW = new SerClass[SerForSerOutput] {
    override def write(a: SerForSerOutput, out: SerOutput) { a.ser.write(a.x, out) }
    override def schema = SerSchemas.XUnknown(SerSchemas.XSerOutput)
    override def gen: Gen[SerForSerOutput] = sys.error("Cannot gen a SerForSerOutput")
    override def read(in: SerInput): SerForSerOutput = READ_ERROR("Cannot read SerForSerOutput")
  }

  def apply[AA: SerWritable](value: AA) = new SerForSerOutput {
    type A = AA
    val x  : A           = value
    val ser: SerWritable[A] = implicitly[SerWritable[A]]
  }

}

trait SerForSerOutput {
  type A
  val x  : A
  val ser: SerWritable[A]
  override def toString = "SerWriteonlyable(" + x + " : " + ser + ")"
}