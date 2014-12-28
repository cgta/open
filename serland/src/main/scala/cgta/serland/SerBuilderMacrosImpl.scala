package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/4/14 10:23 PM
//////////////////////////////////////////////////////////////

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object SerBuilderMacrosImpl {

  def forCaseClass[A <: Product : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]
    val sym = tpe.typeSymbol.asClass
    val tpeStr = tpe.toString

    if (!sym.isCaseClass) {
      c.error(c.enclosingPosition, "Cannot create SerClass for non-case class")
      return c.Expr[SerClass[A]](q"null")
    }

    val schemaName = {
      if (tpeStr.contains("[")) {
        q"None"
      } else {
        q"Some($tpeStr)"
      }
    }

    val accessors = (tpe.decls collect {
      case acc: MethodSymbol if acc.isCaseAccessor => acc
    }).toList

    val serClasses = for {
      (accessor, idx) <- accessors.zipWithIndex
    } yield {
      val serClassName = c.universe.TermName("serClass" + idx)
      val tpeN = c.universe.TypeName(accessor.typeSignature.toString.drop(3))
      //Using parse here is really dirty, however I can't get it to work with quasiquotes
      //Generic parameter names will be treated as concrete, unless c.universe.newTypeName(...) is used
      //However that will cause problems if that is called on something like Option[Int] It doesn't
      //see to be able to make a typeName properly when the type has [, ] in it. Which is probably
      //correct.
      c.parse(s"val $serClassName = implicitly[cgta.serland.SerClass[$tpeN]]")
    }


    val serWriteStmts = for {
      (accessor, idx) <- accessors.zipWithIndex
    } yield {
      val fieldName = accessor.name
      val fieldNameString = fieldName.toString
      val fieldNameTerm = c.universe.TermName(fieldNameString)
      val serWritableName = c.universe.TermName("serClass" + idx)
      val tpeN = accessor.typeSignature
      q"""
      try {
        out.writeFieldBegin($fieldNameString, $idx)
        $serWritableName.write(a.$fieldNameTerm, out)
        out.writeFieldEnd()
      } catch {
        case e : Throwable => cgta.serland.WRITE_ERROR("at Field[" + $fieldNameString + "]", e)
      }
      """
    }

    val serReadStmts = for {
      (accessor, idx) <- accessors.zipWithIndex
    } yield {
      val fieldName = accessor.name
      val fieldNameString = fieldName.toString
      val serReadableName = c.universe.TermName("serClass" + idx)
      val tmpName = c.universe.TermName("tmp" + idx)

      q"""
      val $tmpName = try {
        in.readFieldBegin($fieldNameString, $idx)
        val t = $serReadableName.read(in)
        in.readFieldEnd()
        t
      } catch {
        case e : Throwable => cgta.serland.READ_ERROR("at Field[" + $fieldNameString + "]", e)
      }
      """
    }

    val tmps = for {
      (accessor, idx) <- accessors.zipWithIndex.toList
    } yield {
      val tmpName = c.universe.TermName("tmp" + idx)
      q"$tmpName"
    }

    val serGen = {
      val tStr = tpe.toString
      val gensStr = (0 until accessors.size).map(i=> s"serClass$i.gen.sample.get").mkString("", ",", "")
      //Seems that just returning the type is enough because there seems to be an implicit that will
      //lift the returned type into a Gen[T]
      val code = s" cgta.serland.gen.Arbitrary.arbitrary[Boolean].map(x=> new $tStr($gensStr))"
      c.parse(code)
    }

    val fieldSchemas = {
      for {
        (accessor, idx) <- accessors.zipWithIndex.toList
      } yield {
        val fieldName = accessor.name
        val fieldNameString = fieldName.toString
        val serReadableName = c.universe.TermName("serClass" + idx)
        q"XField($fieldNameString, $idx, $serReadableName.schema.schemaRef)"
      }
    }

    val result = q"""
      new cgta.serland.SerClass[$tpe] {
        ..$serClasses
        override def schema : cgta.serland.SerSchema = {
          import cgta.serland.SerSchemas.{XStruct, XField}
          XStruct($schemaName, Vector(..$fieldSchemas))
        }

        override def read(in: cgta.serland.SerInput) : $tpe = {
          try {
            in.readStructBegin()
            ..$serReadStmts
            in.readStructEnd()
            new $tpe(..$tmps)
          } catch {
            case e : Throwable => cgta.serland.READ_ERROR("at SerCaseClass[" + $tpeStr + "]", e)
          }
        }

        override def write(a: $tpe, out: cgta.serland.SerOutput) {
          try {
            out.writeStructBegin()
            ..$serWriteStmts
            out.writeStructEnd()
          } catch {
            case e : Throwable => cgta.serland.WRITE_ERROR("at SerCaseClass[" + $tpeStr + "]", e)
          }
        }

        override def gen : cgta.serland.gen.Gen[$tpe] = $serGen

      }
    """

    c.Expr[SerClass[A]](result)
  }


  private def forSubsN[A](c: Context)(base: c.Type, subs: IVec[c.Type]): c.Expr[SerClass[A]] = {
    import c.universe._

    def name(x: c.Type) = x.typeSymbol.name.toString

    val baseStr = base.toString

    val schemaName = {
      if (baseStr.contains("[")) {
        q"None"
      } else {
        q"Some($baseStr)"
      }
    }


    locally {
      val names = subs.map(name)
      if (names.toSet.size != names.size) {
        c.error(c.enclosingPosition, "Duplicate names for subtypes: " + names.sorted)
        return c.Expr[SerClass[A]](q"null")
      }
    }

    val vecNmes = for (sub <- subs) yield {
      val n = name(sub)
      q"$n"
    }

    val vecSers = for (sub <- subs) yield {
      q"implicitly[cgta.serland.SerClass[$sub]]"
    }

    val mapSers = for (sub <- subs) yield {
      val n = name(sub)
      q"($n, implicitly[cgta.serland.SerClass[$sub]])"
    }

    val serIdxs = for ((sub, idx) <- subs.zipWithIndex) yield {
      val serIdx = TermName("ser" + idx)
      q"val $serIdx = implicitly[cgta.serland.SerClass[$sub]]"
    }

    val ifs = for ((sub, idx) <- subs.zipWithIndex) yield {
      val n = name(sub)
      val serIdx = TermName("ser" + idx)
      q"""
      if (a.isInstanceOf[$sub]) {
        out.writeOneOfBegin($n,$idx)
        $serIdx.write(a.asInstanceOf[$sub], out)
        out.writeOneOfEnd()
        return
      }
      """
    }

    val subSchemas = {
      for {
        (sub, idx) <- subs.zipWithIndex.toList
      } yield {
        val n = name(sub)
        val serIdx = TermName("ser" + idx)
        q"XSub($n, $idx, $serIdx.schema.schemaRef)"
      }
    }

    val result = q"""
    new cgta.serland.SerClass[$base] {
      override def schema : cgta.serland.SerSchema = {
        import cgta.serland.SerSchemas.{XOneOf, XSub}
        XOneOf($schemaName,Vector(..$subSchemas))
      }


      val vecNmes = scala.collection.immutable.Vector(..$vecNmes)
      val vecSers = scala.collection.immutable.Vector(..$vecSers)
      val mapSers = scala.collection.immutable.Map(..$mapSers)
      ..$serIdxs

      override def read(in: cgta.serland.SerInput) : $base = {
        try {
          val ser = in.readOneOfBegin match {
            case Left(keyStr) =>
              mapSers.get(keyStr).getOrElse(cgta.serland.READ_ERROR("Unknown Key " + keyStr))
            case Right(keyInt) =>
              if (keyInt < 0 || keyInt >= vecSers.size) cgta.serland.READ_ERROR("Unknown Key Ordinal" + keyInt)
              vecSers(keyInt)
          }
          val res = ser.read(in)
          in.readOneOfEnd()
          res
        } catch {
          case e : Throwable => cgta.serland.READ_ERROR("at SerOneOf[" + $baseStr + "]", e)
        }
      }

      override def write(a: $base, out: cgta.serland.SerOutput) {
        try {
          ..$ifs
        } catch {
          case e : Throwable => cgta.serland.WRITE_ERROR("at SerOneOf[" + $baseStr + "]", e)
        }
        cgta.serland.WRITE_ERROR("No subtype of " + $baseStr + " matches the type of " + a.getClass + " " + a)
      }

      override def gen: cgta.serland.gen.Gen[$base] = {
        cgta.serland.gen.Gen.oneOf(vecSers.map(_.gen)).flatMap(x => x)
      }
    }
    """

    c.Expr[SerClass[A]](result)
  }



  def forCase1[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function1[_, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase2[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function2[_, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase3[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function3[_, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase4[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function4[_, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase5[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function5[_, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase6[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function6[_, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase7[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function7[_, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase8[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function8[_, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase9[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function9[_, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase10[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function10[_, _, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase11[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function11[_, _, _, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase12[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function12[_, _, _, _, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase13[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function13[_, _, _, _, _, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)
  def forCase14[A <: Product : c.WeakTypeTag](c: Context)(f: c.Expr[Function14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, A]]): c.Expr[SerClass[A]] = forCaseClass[A](c)


  def forSubs1[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1]))
  def forSubs2[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2]))
  def forSubs3[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3]))
  def forSubs4[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4]))
  def forSubs5[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag, S5 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4], c.weakTypeOf[S5]))
  def forSubs6[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag, S5 <: A : c.WeakTypeTag, S6 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4], c.weakTypeOf[S5], c.weakTypeOf[S6]))
  def forSubs7[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag, S5 <: A : c.WeakTypeTag, S6 <: A : c.WeakTypeTag, S7 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4], c.weakTypeOf[S5], c.weakTypeOf[S6], c.weakTypeOf[S7]))
  def forSubs8[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag, S5 <: A : c.WeakTypeTag, S6 <: A : c.WeakTypeTag, S7 <: A : c.WeakTypeTag,  S8 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4], c.weakTypeOf[S5], c.weakTypeOf[S6], c.weakTypeOf[S7], c.weakTypeOf[S8]))
  def forSubs9[A: c.WeakTypeTag, S1 <: A : c.WeakTypeTag, S2 <: A : c.WeakTypeTag, S3 <: A : c.WeakTypeTag, S4 <: A : c.WeakTypeTag, S5 <: A : c.WeakTypeTag, S6 <: A : c.WeakTypeTag, S7 <: A : c.WeakTypeTag,  S8 <: A : c.WeakTypeTag,  S9 <: A : c.WeakTypeTag](c: Context): c.Expr[SerClass[A]] = forSubsN[A](c)(c.weakTypeOf[A], IVec(c.weakTypeOf[S1], c.weakTypeOf[S2], c.weakTypeOf[S3], c.weakTypeOf[S4], c.weakTypeOf[S5], c.weakTypeOf[S6], c.weakTypeOf[S7], c.weakTypeOf[S8], c.weakTypeOf[S9]))

}

