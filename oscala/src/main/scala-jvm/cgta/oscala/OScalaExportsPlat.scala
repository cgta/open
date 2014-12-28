package cgta.oscala

import scala.concurrent.ExecutionContext


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/16/14 1:05 PM
//////////////////////////////////////////////////////////////

trait OScalaExportsPlat extends OScalaExportsShared {
  override val defaultExecutionContext: ExecutionContext = ExecutionContext.global
}
