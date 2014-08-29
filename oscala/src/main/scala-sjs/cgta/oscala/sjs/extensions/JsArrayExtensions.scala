package cgta.oscala
package sjs.extensions

import scala.collection.GenMap
import scala.reflect.ClassTag
import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 10:37 PM
//////////////////////////////////////////////////////////////

class JsArrayExtensions[A](val xs : js.Array[A]) extends AnyVal {
  def toArray(implicit ev : ClassTag[A]) : Array[A] = {
    Array[A](xs.toSeq : _*)
  }
}