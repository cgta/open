package cgta.oscala
package util

import scala.reflect.macros.blackbox


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/25/15 9:41 AM
//////////////////////////////////////////////////////////////

import scala.language.experimental.macros


object StringMapBuilder {
  def apply[A] : A = macro StringMapBuilderMacros.builderImpl[A]
}

//See
//https://github.com/jducoeur/Querki/blob/master/querki/scalajs/src/main/scala/org/querki/facades/jqueryui/JQueryUIDialog.scala
//https://github.com/jducoeur/Querki/blob/master/querki/scalajs/src/main/scala/org/querki/facades/jqueryui/package.scala
//https://github.com/jducoeur/Querki/tree/master/querki/scalajs/src/main/scala/org/querki/jsext
object StringMapBuilderMacros {
  def builderImpl[A](c : blackbox.Context)(implicit t : c.WeakTypeTag[A]) : c.Expr[A] = {
    ???
  }
}

