package cgta.serland

import cgta.serland.gen.Gen

//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 10/12/11 10:31 PM
//////////////////////////////////////////////////////////////

/**
 * This ser class is used when you want to support partially parsing a message.
 *
 * This class will copy the underlying data structure to create a new serInput
 * on each call to input().
 *
 * This allows you to dispatch to a SerClass.read method dynamically.
 *
 */

object SerForSerInput {
  implicit val ser = new SerClass[SerForSerInput] {
    override def read(in: SerInput): SerForSerInput = SerForSerInput(in.readSerInput())
    override def schema: SerSchema = SerSchemas.XUnknown(SerSchemas.XSerInput)
    override def write(a: SerForSerInput, out: SerOutput): Unit = WRITE_ERROR("Cannot write a SerForSerInput")
    override def gen: Gen[SerForSerInput] = sys.error("Cannot gen a SerForSerInput")
  }
}

case class SerForSerInput(input: () => SerInput)