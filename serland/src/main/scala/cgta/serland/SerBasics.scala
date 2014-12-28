package cgta.serland


import cgta.serland.SerHints.{Ser64Hints, Ser32Hints}
import cgta.serland.gen.{Arbitrary, Gen}


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/1/14 2:41 AM
//////////////////////////////////////////////////////////////

object SerBasics {
  object MapEntrySerable {
    def ser[K: SerClass, V: SerClass] = new SerClass[MapEntrySerable[K, V]] {
      val kSer = serClass[K]
      val vSer = serClass[V]
      override def schema: SerSchema = {
        import SerSchemas._
        XStruct(None, IVec(XField("k", 1, kSer.schema.schemaRef), XField("v", 2, vSer.schema.schemaRef)))
      }
      override def read(in: SerInput): MapEntrySerable[K, V] = {
        in.readStructBegin()
        in.readFieldBegin("k", 1)
        val k = kSer.read(in)
        in.readFieldEnd()

        in.readFieldBegin("v", 2)
        val v = vSer.read(in)
        in.readFieldEnd()
        in.readStructEnd()
        new MapEntrySerable[K, V](k, v)
      }
      override def write(a: MapEntrySerable[K, V], out: SerOutput): Unit = {
        out.writeStructBegin()
        out.writeFieldBegin("k", 1)
        kSer.write(a.k, out)
        out.writeFieldEnd()

        out.writeFieldBegin("v", 2)
        vSer.write(a.v, out)
        out.writeFieldEnd()
        out.writeStructEnd()
      }
      override def gen: Gen[MapEntrySerable[K, V]] = for {
        k <- kSer.gen
        v <- vSer.gen
      } yield {
        MapEntrySerable(k, v)
      }
    }
  }
  case class MapEntrySerable[K, V](k: K, v: V)



  trait SerClasses {
    implicit object BooleanSerClass extends SerClass[Boolean] {
      override def write(a: Boolean, out: SerOutput) = out.writeBoolean(a)
      override def read(in: SerInput): Boolean = in.readBoolean()
      override def schema: SerSchema = SerSchemas.XBoolean()
      override def gen: Gen[Boolean] = Arbitrary.arbitrary[Boolean]
    }

    implicit object ByteSerClass extends SerClass[Byte] {
      override def write(a: Byte, out: SerOutput) = out.writeInt32(a.toInt, hint = Ser32Hints.SVarInt32)
      override def read(in: SerInput): Byte = in.readInt32(hint = Ser32Hints.SVarInt32).toByte
      override def schema: SerSchema = SerSchemas.XNumber(SerSchemas.XByte)
      override def gen: Gen[Byte] = Arbitrary.arbitrary[Byte]
    }

    implicit object CharSerClass extends SerClass[Char] {
      override def write(a: Char, out: SerOutput) = out.writeChar(a)
      override def read(in: SerInput): Char = in.readChar()
      override def schema: SerSchema = SerSchemas.XChar()
      override def gen: Gen[Char] = Arbitrary.arbitrary[Char]
    }

    implicit object IntSerClass extends SerClass[Int] {
      override def write(a: Int, out: SerOutput) = out.writeInt32(a, hint = Ser32Hints.SVarInt32)
      override def read(in: SerInput): Int = in.readInt32(hint = Ser32Hints.SVarInt32)
      override def schema: SerSchema = SerSchemas.XNumber(SerSchemas.XSVarInt32)
      override def gen: Gen[Int] = Arbitrary.arbitrary[Int]
    }

    implicit object LongSerClass extends SerClass[Long] {
      override def write(a: Long, out: SerOutput) = out.writeInt64(a, hint = Ser64Hints.SVarInt64)
      override def read(in: SerInput): Long = in.readInt64(hint = Ser64Hints.SVarInt64)
      override def schema: SerSchema = SerSchemas.XNumber(SerSchemas.XSVarInt64)
      override def gen: Gen[Long] = Arbitrary.arbitrary[Long]
    }

    implicit object DoubleSerClass extends SerClass[Double] {
      override def write(a: Double, out: SerOutput) = out.writeDouble(a)
      override def read(in: SerInput): Double = in.readDouble()
      override def schema: SerSchema = SerSchemas.XNumber(SerSchemas.XDouble)
      override def gen: Gen[Double] = Arbitrary.arbitrary[Double]
    }

    implicit object BigDecimalSerClass extends SerClass[BigDecimal] {
      override def write(a: BigDecimal, out: SerOutput) = out.writeString(a.toString())
      override def read(in: SerInput): BigDecimal = BigDecimal(in.readString())
      override def schema: SerSchema = SerSchemas.XNumber(SerSchemas.XBigDecimal)
      override def gen: Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal]
    }

    implicit object StringSerClass extends SerClass[String] {
      override def write(a: String, out: SerOutput) = out.writeString(a)
      override def read(in: SerInput): String = in.readString()
      override def schema: SerSchema = SerSchemas.XString()
      override def gen: Gen[String] = Arbitrary.arbitrary[String]
    }

    implicit object ByteArraySerClass extends SerClass[Array[Byte]] {
      override def write(a: Array[Byte], out: SerOutput) = out.writeByteArr(a)
      override def read(in: SerInput): Array[Byte] = in.readByteArr()
      override def schema: SerSchema = SerSchemas.XByteArray()
      override def gen: Gen[Array[Byte]] = Arbitrary.arbitrary[Array[Byte]]
    }

    class ListSerClass[A: SerClass] extends SerClass[List[A]] {
      implicit val sca = implicitly[SerClass[A]]
      override def write(a: List[A], out: SerOutput): Unit = out.writeIterable(a, sca)
      override def read(in: SerInput): List[A] = in.readIterable(sca).toList
      override def schema: SerSchema = SerSchemas.XSeq(SerSchemas.XList, sca.schema.schemaRef)
      override def gen: Gen[List[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[List[A]]
      }
    }
    implicit def listSerClass[A: SerClass] = new ListSerClass[A]

    class IVecSerClass[A: SerClass] extends SerClass[IVec[A]] {
      implicit val sca = implicitly[SerClass[A]]
      override def write(a: IVec[A], out: SerOutput): Unit = out.writeIterable(a, sca)
      override def read(in: SerInput): IVec[A] = in.readIterable(sca).toVector
      override def schema: SerSchema = SerSchemas.XSeq(SerSchemas.XIVec, sca.schema.schemaRef)
      override def gen: Gen[IVec[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[IVec[A]]
      }
    }
    implicit def iVecSerClass[A: SerClass] = new IVecSerClass[A]

    class ISetSerClass[A: SerClass] extends SerClass[ISet[A]] {
      implicit val sca = implicitly[SerClass[A]]
      override def write(a: ISet[A], out: SerOutput): Unit = out.writeIterable(a, sca)
      override def read(in: SerInput): ISet[A] = in.readIterable(sca).toSet
      override def schema: SerSchema = SerSchemas.XSeq(SerSchemas.XISet, sca.schema.schemaRef)
      override def gen: Gen[ISet[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[ISet[A]]
      }
    }
    implicit def iSetSerClass[A: SerClass] = new ISetSerClass[A]

    class SeqSerClass[A: SerClass] extends SerClass[scala.collection.Seq[A]] {
      implicit val sca = implicitly[SerClass[A]]
      override def write(a: scala.collection.Seq[A], out: SerOutput): Unit = out.writeIterable(a, sca)
      override def read(in: SerInput): scala.collection.Seq[A] = in.readIterable(sca).toVector
      override def schema: SerSchema = SerSchemas.XSeq(SerSchemas.XASeq, sca.schema.schemaRef)
      override def gen: Gen[scala.collection.Seq[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[scala.collection.Seq[A]]
      }
    }
    implicit def SeqSerClass[A: SerClass] = new SeqSerClass[A]

    class IISeqSerClass[A: SerClass] extends SerClass[IISeq[A]] {
      implicit val sca = implicitly[SerClass[A]]
      override def write(a: IISeq[A], out: SerOutput): Unit = out.writeIterable(a, sca)
      override def read(in: SerInput): IISeq[A] = in.readIterable(sca).toVector
      override def schema: SerSchema = SerSchemas.XSeq(SerSchemas.XASeq, sca.schema.schemaRef)
      override def gen: Gen[IISeq[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[IISeq[A]]
      }
    }
    implicit def IISeqSerClass[A: SerClass] = new IISeqSerClass[A]


    class OptSerClass[A: SerClass] extends SerClass[Option[A]] {
      val sca = implicitly[SerClass[A]]
      override val schema = SerSchemas.XSeq(SerSchemas.XOpt, sca.schema.schemaRef)
      override def write(xs: Option[A], out: SerOutput) = out.writeOption(xs, sca)
      override def read(in: SerInput): Option[A] = in.readOption(sca)
      override def gen: Gen[Option[A]] = {
        implicit val a = Arbitrary(sca.gen)
        Arbitrary.arbitrary[Option[A]]
      }
    }
    implicit def optSerClass[A: SerClass] = new OptSerClass[A]

    class IMapSerClass[A: SerClass, B: SerClass] extends SerClass[IMap[A, B]] {
      implicit val kvSer  = MapEntrySerable.ser[A, B]
      override val schema = SerSchemas.XSeq(SerSchemas.XIMap, kvSer.schema)
      override def gen = {
        implicit val arb0 = Arbitrary(kvSer.gen)
        Arbitrary.arbitrary[List[MapEntrySerable[A, B]]].map(lst => IMap.empty ++ lst.map(kv => kv.k -> kv.v))
      }
      override def write(xs: IMap[A, B], out: SerOutput) =
        out.writeIterable(xs.toList.map(kv => MapEntrySerable(kv._1, kv._2)), kvSer)
      override def read(in: SerInput) = IMap.empty.++(in.readIterable(kvSer).map(kv => kv.k -> kv.v))
    }
    implicit def iMapSerClass[K: SerClass, V: SerClass] = new IMapSerClass[K, V]


    class EitherSerClass[A: SerClass, B: SerClass] extends SerClass[Either[A, B]] {
      lazy val sca = implicitly[SerClass[A]]
      lazy val scb = implicitly[SerClass[B]]
      lazy val scao = implicitly[SerClass[Option[A]]]
      lazy val scbo = implicitly[SerClass[Option[B]]]
      override val schema = SerSchemas.XEither(sca.schema, scb.schema)
      override def gen = {
        implicit val arba = Arbitrary(sca.gen)
        implicit val arbb = Arbitrary(scb.gen)
        Arbitrary.arbEither[A, B].arbitrary
      }
      override def write(xs: Either[A, B], out: SerOutput) = {
        out.writeStructBegin()
        out.writeFieldBegin("left", 1)
        scao.write(xs.left.toOption, out)
        out.writeFieldEnd()
        out.writeFieldBegin("right", 2)
        scbo.write(xs.right.toOption, out)
        out.writeFieldEnd()
        out.writeStructEnd()
      }
      override def read(in: SerInput) : Either[A,B] = {
        var res : Option[Either[A, B]] = None
        in.readStructBegin()
        in.readFieldBegin("left", 1)
        scao.read(in).foreach((a) => res = Some(Left(a)))
        in.readFieldEnd()
        in.readFieldBegin("right", 2)
        scbo.read(in).foreach((b) => res = Some(Right(b)))
        in.readFieldEnd()
        in.readStructEnd()
        res match {
          case Some(r) => r
          case None => READ_ERROR("Unable to read Either")
        }
      }
    }
    implicit def eitherSerClass[A: SerClass, B: SerClass] = new EitherSerClass[A, B]

  }
}
