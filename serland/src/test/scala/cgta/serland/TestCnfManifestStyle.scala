package cgta.serland

import cgta.serland.backends.SerJsonIn
import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/3/14 12:59 PM
//////////////////////////////////////////////////////////////


object TestCnfManifestStyle extends FunSuite {

  object TypeMan {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class TypeMan(`type`: String)

  object CIJavaMan {
    implicit val ser = SerBuilder.forCase(this.apply _)
    val typeStr = "IJava"
  }
  case class CIJavaMan(`type`: String = CIJavaMan.typeStr, server: String, jar: String)

  object COJavaMan {
    implicit val ser = SerBuilder.forCase(this.apply _)
    val typeStr = "OJava"
  }
  case class COJavaMan(`type`: String = COJavaMan.typeStr, server: String, version: String)

  test("DispatchOnTypeField") {
    val i = SerJsonIn.fromJsonString[SerForSerInput]("""{"type":"IJava", "server":"srv-0", "jar":"cgta-foo.jar"}""")
    val res = TypeMan.ser.read(i.input()).`type` match {
      case CIJavaMan.typeStr => CIJavaMan.ser.read(i.input())
      case COJavaMan.typeStr => COJavaMan.ser.read(i.input())
    }
    Assert.isAnyEquals(res, CIJavaMan(server = "srv-0", jar = "cgta-foo.jar"))
  }


}