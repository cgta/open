package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 7/31/14 11:34 PM
//////////////////////////////////////////////////////////////

object ThreadStopper {
  def stop(t : Thread) = DeprecationWorkarounds.stopThread(t)
}