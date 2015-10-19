package cgta.oscala
package util.debugging

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/28/14 9:07 PM
//////////////////////////////////////////////////////////////


trait PrintPlat extends PRINT {
  override final def |(msg: Any) {
    js.Dynamic.global.console.log(msg.asInstanceOf[js.Any])
  }
}