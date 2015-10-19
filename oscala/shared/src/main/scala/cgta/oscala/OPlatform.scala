package cgta.oscala


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/19/14 8:45 PM
//////////////////////////////////////////////////////////////

object OPlatform extends OPlatformImpl with OPlatform

trait OPlatform {
  trait StringUtils {
    def isNumeric(s : String) : Boolean
  }
  val StringUtils : StringUtils
  
  trait SortingUtils {
    //Can still be stable, but doesn't have to be
    def unstableInplace[A](xs : Array[A])(implicit ord : Ordering[A])
  }
  val SortingUtils : SortingUtils

  def getWeakMap[A, B] : MMap[A, B]

  def isScalaJs : Boolean
  def isInJar[A](cls : Class[A]): Option[Boolean]

}