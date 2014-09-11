package cgta.oscala
package util.debugging

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/28/14 9:07 PM
//////////////////////////////////////////////////////////////


trait PrintPlat extends PRINT {
  override final def |(msg: Any) {
    js.Dynamic.global.console.log(msg.asInstanceOf[js.Any])
  }
}