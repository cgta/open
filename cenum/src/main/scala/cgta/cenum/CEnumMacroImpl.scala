package cgta.cenum

import scala.reflect.macros.Context

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/8/14 3:41 PM
//////////////////////////////////////////////////////////////


object CEnumMacroImpl {
  import scala.language.experimental.macros

  def getElementsImpl[A <: CEnum : c.WeakTypeTag](c : Context)(enum : c.Expr[A]): c.Expr[IVec[A#EET]] = {
    import c.universe._

    val tpe = c.weakTypeOf[A]
    val elems = tpe.declarations.filter(d => d.isModule).map(_.name)
    val res = q"scala.collection.immutable.Vector(..$elems)"
    c.Expr[IVec[A#EET]](res)
  }

}