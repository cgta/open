package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/18/15 11:55 AM
//////////////////////////////////////////////////////////////

object Ior {
  case object End extends Ior[Nothing]
  case object Block extends Ior[Nothing]
  case class Data[A](d: A) extends Ior[A]
}
sealed trait Ior[+A]
