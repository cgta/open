package cgta.serland
package rpcs

import concurrent.Future


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/9/14 11:33 PM
//////////////////////////////////////////////////////////////

trait RpcsJvm {self: Rpcs =>

  override def remotely[R](name: String)(implicit serR: SerClass[R]): Future[R] =
    sys.error(s"Please override ${self.baseName} / $name with a JVM Implementation")

  override def remotely[T1, R](name: String, a: T1)(implicit serA: SerClass[T1], serB: SerClass[R]): Future[R] =
    sys.error(s"Please override ${self.baseName} / $name with a JVM Implementation")

}