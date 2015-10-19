package cgta
package serland

import cgta.serland.gen.Gen


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/3/14 10:59 PM
//////////////////////////////////////////////////////////////


//object SerBuilderNoMacro {
//  class SerForCaseObjects[A](caseObjects: IVec[A with Product]) extends SerClass[A] {
//    val nameMap = IMap(caseObjects.map(co => co.toString -> co): _*)
//    override def schema: SerSchema = {
//      import cgta.serland.SerSchemas._
//      XEnum(
//        XCaseObjects,
//        caseObjects.zipWithIndex.map { case (co, idx) =>
//          XEnumElement(co.toString, idx)
//        })
//    }
//    override def read(in: SerInput): A = {
//      nameMap.get(in.readString()).getOrElse {
//        READ_ERROR("Error attempting to read case object from file, valid options " + caseObjects)
//      }
//    }
//    override def write(a: A, out: SerOutput) {
//      out.writeString(a.toString)
//    }
//    override def gen: Gen[A] = Gen.oneOf(nameMap.values.toSeq)
//  }
//
//  private class Field[A](val name: String, val id: Int, val serClass: SerClass[A]) {
//    def write(v: Any, out: SerOutput) {
//      serClass.write(v.asInstanceOf[A], out)
//    }
//  }
//
//  object SerForCaseClasses {
//    def apply[A <: Product : Manifest](fields: IVec[(String, SerClass[_])])(fn: IVec[Any] => A): SerClass[A] = {
//      val fs = fields.zipWithIndex.map { case ((name, sc), idx) => new Field(name, idx, sc)}
//      val manStr = manifest[A].toString()
//      val schemaId = if (manStr.contains("[")) None else Some(manStr)
//      new SerForCaseClasses[A](schemaId, fs, fn)
//    }
//  }
//  class SerForCaseClasses[A <: Product](
//    private val schemaId : Option[String],
//    private val fields: IVec[Field[_]],
//    private val fn: IVec[Any] => A) extends SerClass[A] {
//    override def schema: SerSchema = {
//      import SerSchemas._
//      XStruct(schemaId, fields.map(f => XField(f.name, f.id, f.serClass.schema)))
//    }
//    override def read(in: SerInput): A = {
//      in.readStructBegin()
//      val vs = for (f <- fields) yield {
//        in.readFieldBegin(f.name, f.id)
//        val v = f.serClass.read(in)
//        in.readFieldEnd()
//        v
//      }
//      in.readStructEnd()
//      fn(vs)
//    }
//    override def write(a: A, out: SerOutput) {
//      out.writeStructBegin()
//      for (f <- fields) {
//        out.writeFieldBegin(f.name, f.id)
//        f.write(a.productElement(f.id), out)
//        out.writeFieldEnd()
//      }
//      out.writeStructEnd()
//    }
//    override def gen: Gen[A] = Gen.wrap {
//      val vs = for {
//        f <- fields
//        v <- f.serClass.gen.sample
//      } yield {
//        v
//      }
//      fn(vs)
//    }
//  }
//
//  class SubOfElem[A, B <: A : SerClass](val name: String)(val downcast: PartialFunction[A, B]) {
//    val serClass = cgta.serland.serClass[B]
//    def write[B](a: A, out: SerOutput) {
//      serClass.write(downcast(a), out)
//    }
//  }
//
//  object SubsOf {
//    def apply[A: Manifest](subs: IVec[SubOfElem[A, _]]) = {
//      val manStr = manifest[A].toString()
//      val schemaId = if (manStr.contains("[")) None else Some(manStr)
//      new SubsOf(schemaId, subs)
//    }
//  }
//
//  class SubsOf[A](private val schemaId : Option[String],
//    private val subs: IVec[SubOfElem[A, _]]) extends SerClass[A] {
//    override def schema: SerSchema = {
//      import SerSchemas._
//      XOneOf(schemaId, subs.zipWithIndex.map { case (s, idx) => XSub(s.name, idx, s.serClass.schema.schemaRef)})
//    }
//    override def read(in: SerInput): A = {
//      val sub: SubOfElem[A, _] = in.readOneOfBegin() match {
//        case Left(s) => subs.find(_.name == s).getOrElse(READ_ERROR(s"Unable to find class for key [$s]"))
//        case Right(n) =>
//          if (n < 0 || n >= subs.size)
//            READ_ERROR(s"Unable to find class for ordinal [$n]")
//          else
//            subs(n)
//      }
//      val res = sub.serClass.read(in)
//      in.readOneOfEnd()
//      res.asInstanceOf[A]
//    }
//    override def write(a: A, out: SerOutput) {
//      subs.zipWithIndex.find(_._1.downcast.isDefinedAt(a)) match {
//        case Some((sub, idx)) =>
//          out.writeOneOfBegin(sub.name, idx)
//          sub.write(a, out)
//          out.writeOneOfEnd()
//        case None => WRITE_ERROR(s"Unknown Child Serializer for OneOf for instance [$a]")
//      }
//    }
//    override def gen: Gen[A] = Gen.oneOf(subs.map(_.serClass.gen)).flatMap(x => x.asInstanceOf[A])
//  }
//}
//
///**
// * Designed to be used for serclasses within the serland project. As of
// * 20140403 Macros won't work in the same project they are defined, until that
// * changes this is a useful DSL for providing serclass for types in serland
// * itself. SerSchema is good example of something that needs this functionality.
// */
//trait SerBuilderNoMacro {
//  import SerBuilderNoMacro._
//
//  def forCaseObjects[A](caseObjects: (A with Product)*): SerClass[A] =
//    new SerForCaseObjects[A](caseObjects.toVector)
//
//  def forCaseClass[A <: Product : Manifest]()(fn: () => A) = {
//    SerForCaseClasses(IVec()) {
//      case IVec() => fn()
//    }
//  }
//  def forCaseClass[A <: Product : Manifest, F0: SerClass](n0: String)(fn: (F0) => A) = {
//    SerForCaseClasses(IVec(n0 -> serClass[F0])) {
//      case IVec(v0) => fn(v0.asInstanceOf[F0])
//    }
//  }
//  def forCaseClass[A <: Product : Manifest, F0: SerClass, F1: SerClass](n0: String, n1: String)(fn: (F0, F1) => A) = {
//    SerForCaseClasses(IVec(n0 -> serClass[F0], n1 -> serClass[F1])) {
//      case IVec(v0, v1) => fn(v0.asInstanceOf[F0], v1.asInstanceOf[F1])
//    }
//  }
//  def forCaseClass[A <: Product : Manifest, F0: SerClass, F1: SerClass, F2: SerClass](n0: String, n1: String, n2 : String)(fn: (F0, F1, F2) => A) = {
//    SerForCaseClasses(IVec(n0 -> serClass[F0], n1 -> serClass[F1], n2->serClass[F2])) {
//      case IVec(v0, v1, v2) => fn(v0.asInstanceOf[F0], v1.asInstanceOf[F1], v2.asInstanceOf[F2])
//    }
//  }
//  def sub[A, B <: A : SerClass](name: String)(pf: PartialFunction[A, B]): SubOfElem[A, B] = {
//    new SubOfElem(name)(pf)
//  }
//  def forSubsOf[A : Manifest](subs: SubOfElem[A, _]*) = {
//    SubsOf(subs.toVector)
//  }
//
//
//}