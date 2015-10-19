package cgta.serland

import cgta.serland.testing.UnitTestHelpSerClass
import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/28/14 2:07 AM
//////////////////////////////////////////////////////////////


object TestSerClassOneOfs extends FunSuite with UnitTestHelpSerClass {

  object ConA {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class ConA(x: Int) extends Abs

  object ConB {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class ConB(x: Int) extends Abs

  object ConCSer {
    implicit val ser = SerBuilder.forObject(ConC)
  }
  trait ConCSer
  case object ConC extends Abs with ConCSer

  object Abs {implicit val ser: SerClass[Abs] = SerBuilder.forSubs3[Abs, ConA, ConB, ConC.type]}
  sealed trait Abs

  import SerSchemas._

  def v(x: Abs, json: String, verifyJsonInEqualsJsonOut: Boolean = true) {
    val ser1 = Abs.ser
//    val ser2: SerClass[Abs] = SerBuilder.NoMacros.forSubsOf[Abs](
//      SerBuilder.NoMacros.sub[Abs, ConA]("ConA") { case a: ConA => a},
//      SerBuilder.NoMacros.sub[Abs, ConB]("ConB") { case a: ConB => a},
//      SerBuilder.NoMacros.sub[Abs, ConC.type]("ConC") { case a: ConC.type => a}
//    )
    def fn(n: String) = "cgta.serland.TestSerClassOneOfs." + n
    val schema1 = XOneOf(
      Some(fn("Abs")),
      IVec(
        XSub("ConA", 0, XSchemaRef(fn("ConA"))),
        XSub("ConB", 1, XSchemaRef(fn("ConB"))),
        XSub("ConC", 2, XObject())
      ))

    //Manifests toString encodes uses a dollar sign for nested classes
    //and manifests are used by the NoMacro SerClass builders to get
    //the schemaIds
    val schema2 = schema1.copy(schemaId = Some("cgta.serland.TestSerClassOneOfs$Abs"))


    validate[Abs](x)(json, schema = schema1, verifyJsonInEqualsJsonOut = verifyJsonInEqualsJsonOut)(ser1)
//    validate[Abs](x)(json, schema = schema2, verifyJsonInEqualsJsonOut = verifyJsonInEqualsJsonOut)(ser2)
  }

  ignore("Re-add the NoMacros serializers") {

  }

  test("OneOfs") {
    v(ConA(1), """{"key":"ConA","value":{"x":1}}""")
    v(ConB(1), """{"key":"ConB","value":{"x":1}}""")
    v(ConB(1), """{"key":"ConB","value":{"x":1}}""", verifyJsonInEqualsJsonOut = false)
    v(ConB(1), """{"key":"ConB","value":{"x":1}}""", verifyJsonInEqualsJsonOut = false)
    v(ConB(1), """{"key":"ConB","value":{"x":1}}""", verifyJsonInEqualsJsonOut = false)
    v(ConC, """{"key":"ConC","value":{}}""", verifyJsonInEqualsJsonOut = false)
  }
}