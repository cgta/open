package cgta.oscala
package sjs.extensions

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/14/14 2:56 PM
//////////////////////////////////////////////////////////////


class JsAnyExtensions(val a: js.Any) extends AnyVal {
  def asJsDyn = a.asInstanceOf[js.Dynamic]
  def asJsDynObj = a.asInstanceOf[js.Dynamic with js.Object]
  def asJsObj = a.asInstanceOf[js.Object]
  def asJsDict[A] = a.asInstanceOf[js.Dictionary[A]]
}