package cgta.serland

import cgta.serland.backends.{SerJsonIn, SerPenny}
import cgta.serland.testing.UnitTestHelpSerClass
import cgta.oscala.util.BinaryHelp
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved -- test
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 3/27/14 3:23 PM
//////////////////////////////////////////////////////////////


object TestSerClassBasicSers extends FunSuite with UnitTestHelpSerClass {
  import SerPenny.WTs._
  import SerSchemas._

  test("Boolean"){
    validate(true)("true", pennyVarInt(WSVarInt, 1), schema = XBoolean())
    validate(false)("false", pennyVarInt(WSVarInt, 0), schema = XBoolean())
  }

  test("Byte"){
    def v(b: Int) {
      validate(b.toByte)(b.toString, schema = XNumber(XByte))
    }
    v(-128)
    v(127)
    v(126)
    v(-63)
    v(1)
    v(0)
    v(-1)
  }

  test("Char"){
    def v(c: Char) {
      val q = "\""
      val x = if (c == '"') "\\\"" else c.toString
      validate(c)(q + x + q, pennyVarInt(WSVarInt, c.toInt), schema = XChar())
    }

    v('a')
    v('"')
    v('Z')
    v('-')
    //Some unicode
    v('©')
    v('α')
    v('λ')
  }

  test("Int"){
    def v(x: Int) {
      validate(x)(x.toString, pennyVarInt(WSVarInt, BinaryHelp.encodeZigZag64(x)), schema = XNumber(XSVarInt32))
    }
    v(Int.MaxValue)
    v(2)
    v(1)
    v(0)
    v(-1)
    v(-2)
    v(Int.MinValue)
  }

  test("Long"){
    val q = "\""
    def v(x: Long) {
      validate(x)(q + x.toString + q, pennyVarInt(WSVarInt, BinaryHelp.encodeZigZag64(x)), schema = XNumber(XSVarInt64))
    }
    v(Long.MaxValue)
    v(2L)
    v(1L)
    v(0L)
    v(-1L)
    v(-2L)
    v(Long.MinValue)
  }

  test("JsonLongsFromStringOrNumber"){
    val q = "\""
    Assert.isEquals(1L ,  SerJsonIn.fromJsonString[Long]("1"))
    Assert.isEquals(1L ,  SerJsonIn.fromJsonString[Long](q + "1" + q))
  }

  test("Double"){
    def v(x: Double, jsonable: Boolean = true) {
      validate(x)(x.toString, pennyDouble(x), jsonable = false, schema = XNumber(XDouble))
    }
    //Json doesn't have a representation of +/-Inf or NaN
    //      v(Double.PositiveInfinity, jsonable = false)
    //      v(Double.NegativeInfinity, jsonable = false)
    //Since NaN is != NaN by definition this test doesn't work, I am leaving this
    //as a comment to future developers
    //    v(Double.NaN, jsonable = false)

    v(Double.MaxValue)
    v(math.Pi)
    v(math.E)
    v(2.0)
    v(1.0)
    //      v(Double.MinPositiveValue)
    v(0.0)
    v(-0.0)
    //      v(-Double.MinPositiveValue)
    v(-1.0)
    v(-2.0)
    v(Double.MinValue)
  }

  //    test("BigDecimal"){
  //      def v(x: BigDecimal) {
  //        val q = "\""
  //        validate(x)(q + x.toString + q, pennyString(x.toString()), schema = XNumber(XBigDecimal))
  //      }
  //      v(BigDecimal("1.0"))
  //      v(BigDecimal("10.0"))
  //      v(BigDecimal("10.0000000001"))
  //      v(BigDecimal("0.0000000001"))
  //      v(BigDecimal("-0.0000000001"))
  //    }

  test("String"){
    def v(s: String) {
      val q = "\""
      validate(s)(q + s + q, pennyString(s), schema = XString())
    }
    v("α")
    v("λ")
    v("This is a string")
    v("Yet another string to encode!")
  }

  test("ByteArray"){
    validate(Array[Byte]())("[]", pennyBytes(Array()), toEqualable = _.toList)
    validate(
      Array[Byte](1.toByte, 2.toByte))(
        "[1,2]",
        pennyBytes(Array(1.toByte, 2.toByte)),
        schema = XByteArray(),
        toEqualable = _.toList)
  }

  test("Seqs"){
    def v(xs: List[String], json: String) {
      validate(xs)(json, pennySeq(xs), schema = XSeq(XList, XString()))
      validate(xs.toVector)(json, pennySeq(xs), schema = XSeq(XIVec, XString()))
    }
    v(List[String](), "[]")
    v(List("Hello"), "[\"Hello\"]")
    v(List("Hello", "World!"), "[\"Hello\",\"World!\"]")
  }


  test("IMap"){
    def v[V: SerClass](m: IMap[Int, V], json: String, vSchema: SerSchema) {
      val schema = XSeq(XIMap, XStruct(None, IVec(XField("k", 1, XNumber(XSVarInt32)), XField("v", 2, vSchema))))
      validate(m)(json, schema = schema)
    }
    v(IMap.empty[Int, Int], "[]", XNumber(XSVarInt32))
    v(IMap.empty[Int, Option[Int]], "[]", XSeq(XOpt, XNumber(XSVarInt32)))
    v(IMap[Int, Option[Int]](1 -> None), """[{"k":1}]""", XSeq(XOpt, XNumber(XSVarInt32)))
    v(IMap[Int, Option[Int]](1 -> Some(2)), """[{"k":1,"v":2}]""", XSeq(XOpt, XNumber(XSVarInt32)))
    v(IMap(1 -> 2), """[{"k":1,"v":2}]""", XNumber(XSVarInt32))
    v(IMap(1 -> 2, 3 -> 4), """[{"k":1,"v":2},{"k":3,"v":4}]""", XNumber(XSVarInt32))
  }

  //Top level option encodings cause no end of headaches. Leaving this commented out for now
  //Should revisit this when we need to support them
  //  test("Option"){
  //    validate(Option.empty[String])("")
  //    validate(Option(1))("1")
  //  }


}