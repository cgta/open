package cgta.serland

import backends.SerJsonOut

import scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/10/14 1:12 AM
//////////////////////////////////////////////////////////////

object SerlandExportsPlat {
  class SerlandSjsTypeAExtensions[A](a: A) {
    def toSerJs(implicit serA: SerClass[A]): js.Any = {
      js.Dynamic.global.JSON.parse(SerJsonOut.toJsonCompact(a))
    }
  }
}

trait SerlandExportsPlat {
  implicit def addSerlandSjsTypeAExtensions[A](x: A) = new SerlandExportsPlat.SerlandSjsTypeAExtensions[A](x)
}