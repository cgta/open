package cgta.oscala
package extensions

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.Try


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/10/14 8:59 PM
//////////////////////////////////////////////////////////////

class FutureExtensions[A](val f: Future[A]) extends AnyVal {

  def tryResult(implicit ec : ExecutionContext) : Future[Try[A]] = {
    val p = Promise[Try[A]]()
    f.onComplete(t=>p.complete(Try(t)))
    p.future
  }

}