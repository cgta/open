package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/1/14 2:39 AM
//////////////////////////////////////////////////////////////

object SerSchemas {
//  import cgta.serland.SerBuilder.NoMacros._

  object XSeqType {
//    implicit val ser = forCaseObjects[XSeqType](XOpt, XIMap, XASeq, XIVec, XList, XISet)
  }
  sealed trait XSeqType
  case object XOpt extends XSeqType
  case object XIMap extends XSeqType
  case object XASeq extends XSeqType
  case object XIVec extends XSeqType
  case object XList extends XSeqType
  case object XISet extends XSeqType

  object XNumberType {
//    implicit val ser = forCaseObjects[XNumberType](XByte, XSVarInt32, XUVarInt32, XSVarInt64,      XUVarInt64, XFixed64, XDouble, XBigDecimal, XDeci)
  }
  sealed trait XNumberType
  case object XByte extends XNumberType
  case object XSVarInt32 extends XNumberType
  case object XUVarInt32 extends XNumberType
  case object XSVarInt64 extends XNumberType
  case object XUVarInt64 extends XNumberType
  case object XFixed64 extends XNumberType
  case object XDouble extends XNumberType
  case object XBigDecimal extends XNumberType
  case object XDeci extends XNumberType

//  object XEnumType {implicit val ser = forCaseObjects[XEnumType](XCEnum, XCaseObjects)}
  sealed trait XEnumType
  case object XCEnum extends XEnumType
  case object XCaseObjects extends XEnumType

//  object XUnknownType {implicit val ser = forCaseObjects[XUnknownType](XProxy, XSerInput, XSerOutput)}
  sealed abstract class XUnknownType
  case object XProxy extends XUnknownType
  case object XSerInput extends XUnknownType
  case object XSerOutput extends XUnknownType


//  object XField {implicit val ser = forCaseClass("name", "idx", "schema")(XField.apply)}
  case class XField(name: String, idx: Int, schema: SerSchema)
//  object XSub {implicit val ser = forCaseClass("name", "idx", "schema")(XSub.apply)}
  case class XSub(name: String, idx: Int, schema: SerSchema)
//  object XEnumElement {implicit val ser = forCaseClass("name", "idx")(XEnumElement.apply)}
  case class XEnumElement(name: String, idx: Int)

  //Actual children of SerSchema Start Here
  object XStruct {
//    implicit val ser = forCaseClass("schemaId", "fields")(XStruct.apply)
    def make(schemaId: Option[String])(fs: (String, SerSchema)*) = {
      val fields = fs.toVector.zipWithIndex.map { case ((name, schema), index) => XField(name, index, schema)}
      XStruct(schemaId, fields)
    }
  }
  case class XStruct(schemaId: Option[String], fields: IVec[XField]) extends SerSchema {
    override def schemaRef = schemaId.map(XSchemaRef(_)).getOrElse(this)
  }
//  object XObject {implicit val ser = forCaseClass()(XObject.apply)}
  case class XObject() extends SerSchema
//  object XOneOf {implicit val ser = forCaseClass("schemaId", "subs")(XOneOf.apply)}
  case class XOneOf(schemaId: Option[String], subs: IVec[XSub]) extends SerSchema {
    override def schemaRef = schemaId.map(XSchemaRef(_)).getOrElse(this)
  }
//  object XSeq {implicit val ser = forCaseClass("seqType", "schema")(XSeq.apply)}
  case class XSeq(seqType: XSeqType, schema: SerSchema) extends SerSchema

  case class XEither(left : SerSchema, right : SerSchema) extends SerSchema
//  object XEnum {implicit val ser = forCaseClass("enumType", "elements")(XEnum.apply)}
  case class XEnum(enumType: XEnumType, elements: Seq[XEnumElement]) extends SerSchema
//  object XBoolean {implicit val ser = forCaseClass()(XBoolean.apply)}
  case class XBoolean() extends SerSchema
//  object XChar {implicit val ser = forCaseClass()(XChar.apply)}
  case class XChar() extends SerSchema
//  object XNumber {implicit val ser = forCaseClass("numType")(XNumber.apply)}
  case class XNumber(numType: XNumberType) extends SerSchema
//  object XString {implicit val ser = forCaseClass()(XString.apply)}
  case class XString() extends SerSchema
//  object XByteArray {implicit val ser = forCaseClass()(XByteArray.apply)}
  case class XByteArray() extends SerSchema
  /**
   * Unknown types are for things like Proxy, SerForSerInput, SerForSerOutput
   */
//  object XUnknown {implicit val ser = forCaseClass("unknownType")(XUnknown.apply)}
  case class XUnknown(unknownType: XUnknownType) extends SerSchema
  /**
   * A reference to a schema by its schema id
   */
//  object XSchemaRef {implicit val ser = forCaseClass("schemaId")(XSchemaRef.apply)}
  case class XSchemaRef(schemaId: String) extends SerSchema

}


object SerSchema {
//  import SerBuilder.NoMacros._
//  implicit val ser: SerClass[SerSchema] = forSubsOf[SerSchema](
//    sub[SerSchema, SerSchemas.XStruct]("XObject") { case x: SerSchemas.XStruct => x},
//    sub[SerSchema, SerSchemas.XObject]("XObject") { case x: SerSchemas.XObject => x},
//    sub[SerSchema, SerSchemas.XOneOf]("XOneOf") { case x: SerSchemas.XOneOf => x},
//    sub[SerSchema, SerSchemas.XSeq]("XSeq") { case x: SerSchemas.XSeq => x},
//    sub[SerSchema, SerSchemas.XEnum]("XEnum") { case x: SerSchemas.XEnum => x},
//    sub[SerSchema, SerSchemas.XBoolean]("XBoolean") { case x: SerSchemas.XBoolean => x},
//    sub[SerSchema, SerSchemas.XChar]("XChar") { case x: SerSchemas.XChar => x},
//    sub[SerSchema, SerSchemas.XNumber]("XNumber") { case x: SerSchemas.XNumber => x},
//    sub[SerSchema, SerSchemas.XByteArray]("XByteArray") { case x: SerSchemas.XByteArray => x},
//    sub[SerSchema, SerSchemas.XUnknown]("XUnknown") { case x: SerSchemas.XUnknown => x},
//    sub[SerSchema, SerSchemas.XSchemaRef]("XSchemaRef") { case x: SerSchemas.XSchemaRef => x}
//  )
}

sealed trait SerSchema {
  def schemaRef: SerSchema = this
}



