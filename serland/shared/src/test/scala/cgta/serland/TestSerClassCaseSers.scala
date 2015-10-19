package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/27/14 10:57 PM
//////////////////////////////////////////////////////////////


import cgta.oscala.OPlatform
import cgta.serland.testing.UnitTestHelpSerClass
import cgta.otest.FunSuite

object TestSerClassCaseSers extends FunSuite with UnitTestHelpSerClass {
  import SerBuilder._
  import SerSchemas._

  object ZeroElementCaseClass {implicit val ser = forCase(this.apply _)}
  case class ZeroElementCaseClass()

  object Pointish {implicit val ser = forCase(this.apply _)}
  case class Pointish(x: Int, y: Int, z: Int)

  object SimpleOption {implicit val ser = forCase(this.apply _)}
  case class SimpleOption(x: Option[Int])

  object NestedOption {implicit val ser = forCase(this.apply _)}
  case class NestedOption(x: Option[Option[List[Option[Int]]]])

  object TypeParams {implicit def ser[A: SerClass]: SerClass[TypeParams[A]] = forCaseClass[TypeParams[A]]}
  case class TypeParams[A](x: A)

  object NestedTypeParams {
    implicit def ser[A: SerClass]: SerClass[NestedTypeParams[A]] = forCaseClass[NestedTypeParams[A]]
  }
  case class NestedTypeParams[A](xs: List[A])

  object SuperNestedOption {implicit val ser = forCase(this.apply _)}
  case class SuperNestedOption(
    v: List[List[Int]],
    w: Option[Option[Option[List[Option[Int]]]]],
    x: Double,
    y: Option[Option[String]],
    z: Option[Option[Int]])

  object Pointishs {implicit val ser = forCase(this.apply _)}
  case class Pointishs(a: List[Pointish], b: Pointish, c: SuperNestedOption, d: ZeroElementCaseClass)


  object RecursiveA {implicit def serA: SerClass[RecursiveA] = proxy(forCase(RecursiveA.apply _))}
  case class RecursiveA(r: Option[RecursiveB])

  object RecursiveB {implicit def serB: SerClass[RecursiveB] = proxy(forCase(RecursiveB.apply _))}
  case class RecursiveB(r: Option[RecursiveA])


  val sampleCnt = if (OPlatform.isScalaJs) 2 else 20

  def genValidate[A: SerClass] {
    val sc = implicitly[SerClass[A]]
    for (i <- 0 to sampleCnt) {
      val instance = sc.gen.sample.get
      validate(instance)()
    }
  }

  val pre = "cgta.serland.TestSerClassCaseSers"
  def xstruct(name: String)(fs: (String, SerSchema)*) = {
    val n = if (name.isEmpty) None else Some(s"$pre.$name")
    XStruct.make(n)(fs: _*)
  }

  val zeccSchema = xstruct("")()

  object sno {
    val v = XSeq(XList, XSeq(XList, XNumber(XSVarInt32)))
    val w = XSeq(XOpt, XSeq(XOpt, XSeq(XOpt, XSeq(XList, XSeq(XOpt, XNumber(XSVarInt32))))))
    val x = XNumber(XDouble)
    val y = XSeq(XOpt, XSeq(XOpt, XString()))
    val z = XSeq(XOpt, XSeq(XOpt, XNumber(XSVarInt32)))

    val fs = IVec("v" -> v, "w" -> w, "x" -> x, "y" -> y, "z" -> z)

    val sample    = SuperNestedOption(Nil, None, 1.5, None, None)
    val json      = """{"v":[],"x":1.5}"""
    val schema    = xstruct("SuperNestedOption")(fs: _*)
    val schemaRef = XSchemaRef(schema.schemaId.get)
  }


    test("ZeroElementCaseClass"){
      validate(ZeroElementCaseClass())("""{}""", schema = zeccSchema)
      genValidate[ZeroElementCaseClass]
    }

    test("Pointish"){
      val pointishSchema = locally {
        val n = XNumber(XSVarInt32)
        val fs = IVec("x", "y", "z")
        xstruct("Pointish")(fs.map(_ -> n): _*)
      }

      validate(Pointish(1, 2, 3))("""{"x":1,"y":2,"z":3}""", schema = pointishSchema)
      genValidate[Pointish]
    }

    test("SimpleOption"){
      validate(SimpleOption(None))("""{}""", schema = xstruct("SimpleOption")("x" -> XSeq(XOpt, XNumber(XSVarInt32))))
      validate(SimpleOption(Some(1)))("""{"x":1}""")
      genValidate[SimpleOption]
    }

    test("NestedOption"){
      val xs = XSeq(XOpt, XSeq(XOpt, XSeq(XList, XSeq(XOpt, XNumber(XSVarInt32)))))
      validate(NestedOption(None))("""{}""", schema = xstruct("NestedOption")("x" -> xs))
      validate(NestedOption(Some(None)))("""{"x":[]}""")
      validate(NestedOption(Some(Some(Nil))))("""{"x":[[]]}""")
      validate(NestedOption(Some(Some(List(None)))))("""{"x":[[[]]]}""")
      validate(NestedOption(Some(Some(List(Some(1), None)))))("""{"x":[[[1],[]]]}""")
      genValidate[NestedOption]
    }

    test("SuperNestedOption"){
      validate(sno.sample)(json = sno.json, schema = sno.schema)
      genValidate[SuperNestedOption]
    }

    test("TypeParams"){

      validate(TypeParams(1))("""{"x":1}""", schema = xstruct("")("x" -> XNumber(XSVarInt32)))
      validate(TypeParams("a"))("""{"x":"a"}""", schema = xstruct("")("x" -> XString()))
      genValidate[TypeParams[Int]]
      genValidate[TypeParams[String]]
    }

    test("NestedTypeParams"){
      validate(NestedTypeParams(List(1, 2)))("""{"xs":[1,2]}""",
        schema = xstruct("")("xs" -> XSeq(XList, XNumber(XSVarInt32))))
      validate(NestedTypeParams(List(TypeParams(1))))("""{"xs":[{"x":1}]}""",
        schema = xstruct("")("xs" -> XSeq(XList, xstruct("")("x" -> XNumber(XSVarInt32)))))
      validate(NestedTypeParams(List(TypeParams("a"))))("""{"xs":[{"x":"a"}]}""",
        schema = xstruct("")("xs" -> XSeq(XList, xstruct("")("x" -> XString()))))
      genValidate[NestedTypeParams[Int]]
      genValidate[NestedTypeParams[String]]
    }

    test("Pointishs"){
      val p = Pointish(1, 2, 3)
      val pj = """{"x":1,"y":2,"z":3}"""
      val pSchemaRef = XSchemaRef(s"$pre.Pointish")

      validate(Pointishs(List(p, p), p, sno.sample, ZeroElementCaseClass()))(
        s"""{"a":[$pj,$pj],"b":$pj,"c":${sno.json},"d":{}}""",
        schema = xstruct("Pointishs")("a" -> XSeq(XList, pSchemaRef), "b" -> pSchemaRef, "c" -> sno.schemaRef, "d" -> zeccSchema))
      genValidate[Pointishs]
    }

    test("Recursion"){
      //Some day make this work
      //xstruct("RecursiveA")("r" -> XSchemaRef(s"$pre.RecursiveB"))
      validate(RecursiveA(None))("""{}""", schema = SerSchemas.XUnknown(SerSchemas.XProxy))
      validate(RecursiveB(None))("""{}""", schema = SerSchemas.XUnknown(SerSchemas.XProxy))
      validate(RecursiveB(Some(RecursiveA(Some(RecursiveB(None))))))("""{"r":{"r":{}}}""")
      validate(RecursiveA(Some(RecursiveB(Some(RecursiveA(None))))))("""{"r":{"r":{}}}""")
    }



}