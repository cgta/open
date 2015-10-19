package cgta.oscala
package util

import scala.concurrent.ExecutionContext


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/11/14 8:04 AM
//////////////////////////////////////////////////////////////

trait ConcHelpPlat extends ConcHelp {
  final override lazy val defaultExecutionContext: ExecutionContext = ExecutionContext.global
}