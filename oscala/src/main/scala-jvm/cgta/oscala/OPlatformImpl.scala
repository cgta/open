package cgta.oscala

import cgta.oscala.impls.NumberUtils


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 8:49 PM
//////////////////////////////////////////////////////////////


trait OPlatformImpl extends OPlatform {

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

}
