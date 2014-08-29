package cgta.oscala
package util.debugging


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/28/14 9:05 PM
//////////////////////////////////////////////////////////////

trait PRINT {
  def |(msg : Any) : Unit
}

object PRINT extends PRINT with PrintPlat {

}