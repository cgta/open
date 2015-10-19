package cgta.serland

import cgta.serland.gen.Gen


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 1/7/11 3:03 PM
//////////////////////////////////////////////////////////////

object SerClass extends SerBasics.SerClasses

trait SerClass[A] extends SerWritable[A] with SerReadable[A] with SerSchemable[A] with SerGenable[A] {
  def copy(
    schemaFn: () => SerSchema = () => this.schema,
    readFn: SerInput => A = this.read,
    writeFn: (A, SerOutput) => Unit = this.write,
    genFn: () => Gen[A] = () => this.gen): SerClass[A] = new SerClass[A] {
    
    override def schema: SerSchema = schemaFn()
    override def read(in: SerInput): A = readFn(in)
    override def write(a: A, out: SerOutput): Unit = writeFn(a, out)
    override def gen: Gen[A] = genFn()
  }

}



