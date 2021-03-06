package cgta.serland
package testing

import java.io.ByteArrayOutputStream

import cgta.serland.backends.{SerPennyIn, SerPennyOut, SerPenny}
import cgta.oscala.util.BinaryHelp
import cgta.otest.Asserts

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved -- test
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 3/27/14 6:09 PM
//////////////////////////////////////////////////////////////


trait UnitTestHelpSerClass {
  import SerPenny.WTs._

  final def verify[A, B](expected: A, hint: String = null)(actual: B)(implicit ev: A =:= B) = {
    Asserts.isEquals(expected, actual, hint)
  }

  val encoders: UnitTestEncoders = UnitTestEncodersImpl

  def validate[A: SerClass](instance: A)(
    json: String = null,
    penny: Array[Byte] = null,
    schema: SerSchema = null,
    jsonable: Boolean = true,
    verifyJsonInEqualsJsonOut: Boolean = true,
    toEqualable: A => Any = null) {

    try {
      if (schema != null) {
        verify(schema, "Schemas not equal")(serSchema[A])
      }

      def f(a: A) = if (toEqualable != null) toEqualable(a) else a

      if (jsonable) {
        val jsonEncoder = encoders.json
        val jsonOut = jsonEncoder.encode(instance)
        verify(f(instance), "Json Failure: " + jsonOut)(f(jsonEncoder.decode[A](jsonOut)))
        if (json != null) {
          val parsed = jsonEncoder.decode[A](json)
          verify(f(instance), "Supplied Json Does Not Parse to expected")(f(parsed))
          if (verifyJsonInEqualsJsonOut) {
            verify(jsonOut, "Supplied Json Not the same as JsonOut")(json)
          }
        }
      }


      if (jsonable && encoders.bson.isDefined) {
        val bsonEncoder = encoders.bson.get
        val mongoOut = bsonEncoder.encode(instance)
        verify(f(instance), "Bson Failure: " + mongoOut)(f(bsonEncoder.decode(mongoOut)))
      }

      locally {
        val pennyOut = SerPennyOut.toByteArray(instance)
        val newInstance = f(SerPennyIn.fromByteArray(pennyOut))
        verify(f(instance), "Penny Failure: " + pennyOut.toList)(newInstance)
        if (penny != null) {
          val parsed = SerPennyIn.fromByteArray[A](penny)
          verify(f(instance), "Supplied Penny Does Not Parse to expected")(f(parsed))
          verify(pennyOut.toList, "Supplied Penny not the same as PennyOut")(penny.toList)
        }
      }

      locally {
        val encoder = encoders.ast
        val ast = encoder.encode(instance)
        verify(f(instance), "AST Failure: " + ast)(f(encoder.decode(ast)))
      }

    } catch {
      case e: Throwable =>
        throw new RuntimeException(s"validate failed $instance", e)
    }

  }

  def pennyVarInt(wt: WT, out: Long) = {
    val baos = new ByteArrayOutputStream()
    baos.write(wt.wtInt)
    BinaryHelp.ToOutputStream.writeUVar(out)(baos)
    baos.toByteArray
  }

  def pennyDouble(out: Double) = {
    //    val baos = new ByteArrayOutputStream()
    //    baos.write(WFixed64.wtInt)
    //    BinaryHelp.OutStream.writeDouble(out)(baos)
    //    baos.toByteArray
    pennyString(out.toString)
  }

  def pennyString(s: String) = {
    pennyBytes(s.getBytesUTF8)
  }

  def pennyBytes(bytes: Array[Byte]) = {
    val baos = new ByteArrayOutputStream()
    baos.write(WByteArray.wtInt)
    BinaryHelp.ToOutputStream.writeUVar(bytes.length)(baos)
    BinaryHelp.ToOutputStream.writeByteArray(bytes)(baos)
    baos.toByteArray
  }

  def pennySeq(xs: Seq[String]) = {
    val baos = new ByteArrayOutputStream()
    baos.write(WList.wtInt)
    BinaryHelp.ToOutputStream.writeUVar(xs.length)(baos)

    if (xs.nonEmpty) {
      baos.write(WByteArray.wtInt)
    }

    for (x <- xs) {
      val bs = x.getBytesUTF8
      BinaryHelp.ToOutputStream.writeUVar(bs.length)(baos)
      BinaryHelp.ToOutputStream.writeByteArray(bs)(baos)
    }
    baos.toByteArray
  }


}