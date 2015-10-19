package cgta.serland

import cgta.serland.backends.{SerJsonIn, SerJsonOut, JsonDerefer}
import cgta.serland.json.{JsonIO, JsonNodes}
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/25/13 10:47 PM
//////////////////////////////////////////////////////////////




/**
 * This class will do quite a bit of testing on the serializers
 * to ensure that SerPenny, SerBson, SerJson all work as expected.
 *
 */
object TestJsonRefs extends FunSuite {
  object UnparsedObjs {
    object SampleB {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleB(x: Int, y: String)
    object SampleAP {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleAP(x: String, y: SampleB)
    object SampleAU {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleAU(x: String, y: SerForSerInput)
  }


    test("RefJsonOnly"){
      import TestJsonRefs.UnparsedObjs._
      val s = """{"x":"Hello","y":{"$ref":"some.ref"}}"""
      val b = SampleB(5, "Hiya")
      val a = SampleAP("Hello", b)
      var paths: List[String] = Nil
      val derefer = new JsonDerefer {
        def getNode(jpath: String): JsonNodes.Value = {
          paths ::= jpath
          val ss = SerJsonOut.toJsonCompact(b)
          JsonIO.read(ss)
        }
      }
      val res = SerJsonIn.fromJsonString[SampleAP](s, derefer)

      Assert.isEquals(a ,  res)
      Assert.isEquals(List("some.ref") ,  paths)
    }

}