package cgta.oscala

import cgta.oscala.impls.NumberUtils
import cgta.oscala.util.debugging.PRINT

import scala.collection.mutable


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/19/14 8:49 PM
//////////////////////////////////////////////////////////////



class OPlatformImpl extends OPlatform {

  override val StringUtils = new StringUtils {
    override def isNumeric(s: String): Boolean = NumberUtils.isNumber(s)
  }

  override val SortingUtils = new SortingUtils {
    //Can still be stable, but doesn't have to be
    override def unstableInplace[A](xs: Array[A])(implicit ord: Ordering[A]) {
      val ys = xs.sorted
      Array.copy(src = ys, srcPos = 0, dest = xs, destPos = 0, ys.length)
    }
  }
  override def getWeakMap[A, B]: MMap[A, B] = mutable.WeakHashMap.empty[A,B]

  override def isScalaJs: Boolean = false
  override def isInJar[A](cls : Class[A]): Option[Boolean] = {
    //Files served by sbt are served from the current directory
    //Files from jars are served from files
    try {
      if (cls == null || cls.getProtectionDomain == null || cls.getProtectionDomain.getCodeSource == null) {
        None
      } else {
        Some(!cls.getProtectionDomain.getCodeSource.getLocation.getFile.endsWith("/"))
      }
    } catch {
      case e : Throwable => None
    }
  }
}
