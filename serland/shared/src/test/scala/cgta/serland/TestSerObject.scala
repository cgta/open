package cgta.serland

import cgta.serland.SerSchemas.XObject
import cgta.serland.testing.UnitTestHelpSerClass
import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/22/14 9:50 AM
//////////////////////////////////////////////////////////////


object TestSerObject extends FunSuite with UnitTestHelpSerClass {

  object ASerableObjectSer {implicit val ser = SerBuilder.forObject(ASerableObject)}
  trait ASerableObjectSer
  case object ASerableObject extends ASerableObjectSer


  test("CaseObjects") {
    validate(ASerableObject)("""{}""", schema = XObject())
  }

}