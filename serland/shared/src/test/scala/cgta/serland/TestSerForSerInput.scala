package cgta.serland

import cgta.oscala.OPlatform
import cgta.serland.testing.UnitTestEncodersImpl
import cgta.otest.FunSuite

//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/2/14 12:20 AM
//////////////////////////////////////////////////////////////


object TestSerForSerInput extends FunSuite {
  import SerSchemas._

  object UnparsedObjs {
    object SampleB {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleB(x: Int, y: String)
    object SampleAP {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleAP(x: String, y: SampleB)
    object SampleAU {implicit val ser = SerBuilder.forCase(this.apply _)}
    case class SampleAU(x: String, y: SerForSerInput)
  }
  val sampleCount = if (OPlatform.isScalaJs) 2 else 20


  import UnparsedObjs._
  test("Unparsed") {
    val pre = "cgta.serland.TestSerForSerInput.UnparsedObjs"
    val schema = XStruct.make(Some(s"$pre.SampleAU"))("x" -> XString(), "y" -> XUnknown(XSerInput))
    Assert.isAnyEquals(schema, SampleAU.ser.schema)

    for (t <- UnitTestEncodersImpl.all; i <- 0 until sampleCount) {
      val sample = SampleAP.ser.gen.sample.get
      val unparsed = t.encodeDecode[SampleAP, SampleAU](sample)
      val bparsed1 = SampleB.ser.read(unparsed.y.input())
      val bparsed2 = SampleB.ser.read(unparsed.y.input())
      Assert.isEquals(sample.y, bparsed1)
      Assert.isEquals(sample.y, bparsed2)
    }
  }
}