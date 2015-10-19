package cgta.oscala
package util.debugging


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/28/14 9:05 PM
//////////////////////////////////////////////////////////////

trait PRINT {
  def |(msg : Any) : Unit
}

object PRINT extends PRINT with PrintPlat {

}