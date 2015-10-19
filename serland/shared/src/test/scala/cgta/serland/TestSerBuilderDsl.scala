package cgta.serland

import cgta.serland.testing.UnitTestHelpSerClass
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/3/14 11:38 PM
//////////////////////////////////////////////////////////////


object TestSerBuilderDsl extends FunSuite with UnitTestHelpSerClass {

  ignore("disabled serbuilder no macros tests") {

  }

//  import SerBuilder.NoMacros._
//  import SerSchemas._
//
//
//  object Color {
//    implicit val ser = forCaseObjects[Color](Red, Pink, Green)
//  }
//  sealed trait Color
//  case object Red extends Color
//  case object Pink extends Color
//  case object Green extends Color
//
//  object Point {implicit val ser = forCaseClass("x", "y")(apply)}
//  case class Point(x: Int, y: Int)
//
//
//  val sampleCount = 100
//
//  test("forCaseObjects") {
//    val schema = XEnum(XCaseObjects, IVec(XEnumElement("Red", 0), XEnumElement("Pink", 1), XEnumElement("Green", 2)))
//    validate[Color](Red)("\"Red\"", schema = schema)
//    validate[Color](Pink)("\"Pink\"", schema = schema)
//    validate[Color](Green)("\"Green\"", schema = schema)
//  }
//
//  test("forCaseClass") {
//    val schema = XStruct(Some("cgta.serland.TestSerBuilderDsl$Point"), IVec(
//      XField("x", 0, XNumber(XSVarInt32)),
//      XField("y", 1, XNumber(XSVarInt32))))
//    validate[Point](Point(1, 2))("""{"x":1,"y":2}""", schema = schema)
//    for (i <- 0 until sampleCount) {
//      validate(Point.ser.gen.sample.get)()
//    }
//  }
}