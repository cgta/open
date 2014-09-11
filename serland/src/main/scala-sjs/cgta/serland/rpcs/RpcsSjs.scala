package cgta.serland
package rpcs

import org.scalajs.dom.extensions.Ajax

import concurrent.Future
import scalajs.concurrent.JSExecutionContext


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/9/14 10:22 PM
//////////////////////////////////////////////////////////////

trait RpcsSjs {self: Rpcs =>
  import JSExecutionContext.Implicits.runNow
  override def remotely[R](name: String)(implicit serR: SerClass[R]): Future[R] = {
    Ajax.post(s"/rpcs/${self.baseName}/$name", "").map(_.responseText.fromJson(serR))
  }
  final override def remotely[T1, R](name: String, a: T1)(implicit serT1: SerClass[T1], serR: SerClass[R]): Future[R] = {
    Ajax.post(s"/rpcs/${self.baseName}/$name", a.toJsonCompact()).map(_.responseText.fromJson(serR))
  }
}