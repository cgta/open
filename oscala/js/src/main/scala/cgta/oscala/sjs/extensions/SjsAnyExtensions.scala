package cgta.oscala
package sjs.extensions

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/22/14 2:00 AM
//////////////////////////////////////////////////////////////


class SjsAnyExtensions(val x : Any) extends AnyVal {

  def asJsAny = x.asInstanceOf[js.Any]

}