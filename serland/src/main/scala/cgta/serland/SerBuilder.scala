package cgta.serland

import cgta.serland.gen.Gen

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/25/13 9:26 PM
//////////////////////////////////////////////////////////////

object SerBuilder extends SerBuilderMacros {

  /**
   * Allows converting a class to/from a class that already has a SerClass, so as to reuse that classes
   * serialization. The typical pattern is to convert some non case class into a case class for
   * serialization. If a more complicated scheme is desired ( for performance for example) then that
   * class should be given manually generated versions of SerClass, you know, for performance.
   */
  def reuse[A, B: SerClass](toReused: A => B, fromReused: B => A): SerClass[A] = new SerClass[A] {
    val underlying = implicitly[SerClass[B]]
    override def schema = underlying.schema
    override def read(in: SerInput) = fromReused(underlying.read(in))
    override def write(a: A, out: SerOutput) { underlying.write(toReused(a), out) }
    override def gen: Gen[A] = underlying.gen.map(fromReused)
    override def toString = s"SerReused Underlying: $underlying"
  }

  //Old name should be removed eventually
  def reuseSerial[A, B: SerClass](toReused: A => B, fromReused: B => A): SerClass[A] = reuse(toReused, fromReused)

  /**
   * Encode a base class as one of it's subclasses, useful for encoding traits that have only 1 impl case class
   * there is less boilerplate required than reuseSerial requires
   *
   */
  def useSubclass[A, B <: A : SerClass](downcast: A => B): SerClass[A] = reuse[A, B](downcast, x => x)

  // This is the answer to recursive relationship problems.
  def proxy[A](f: => SerClass[A]): SerClass[A] = SerClassProxy[A](() => f)

//  object NoMacros extends SerBuilderNoMacro

  def forObject[A](instance: A): SerClass[A] = new SerClass[A] {
    override def schema: SerSchema = SerSchemas.XObject()
    override def gen: Gen[A] = Gen.const(instance)
    override def read(in: SerInput): A = {
      in.readStructBegin()
      in.readStructEnd()
      instance
    }
    override def write(a: A, out: SerOutput): Unit = {
      out.writeStructBegin()
      out.writeStructEnd()
    }
  }

  //Zero Element is special case, just write by hand
  def forCase[A <: Product](f: Function0[A]): SerClass[A] = new SerClass[A] {
    override def schema: SerSchema = SerSchemas.XStruct(None, IVec.empty)
    override def gen: Gen[A] = Gen.resultOf[Int, A]((x) => f())
    override def read(in: SerInput): A = {
      in.readStructBegin()
      in.readStructEnd()
      f()
    }
    override def write(a: A, out: SerOutput): Unit = {
      out.writeStructBegin()
      out.writeStructEnd()
    }
  }

}

