package cgta.oscala
package util

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/18/14 4:30 PM
//////////////////////////////////////////////////////////////

object OBinarySearchLocations {

  def find[A: Ordering](xs: Seq[A], x: A) = fromBs(xs.size, OBinarySearch.bs(xs, x))

  def fromBs(sz: Int, i: Int): OBinarySearchLocation = {
    if (i < 0) {
      val x = -(i + 1)
      if (x < sz) {
        if (x == 0) {
          B_HD
        } else {
          B_LT(x)
        }
      } else {
        B_TL(sz)
      }
    } else {
      B_EQ(i)
    }
  }

  sealed trait OBinarySearchLocation {
    def beforeOrAt: Int
    def foundAt: Option[Int] = None
  }
  /**
   * Less than the element @i=0
   * x < xs(0)
   */
  case object B_HD extends OBinarySearchLocation {
    override def beforeOrAt = 0
  }
  /**
   * Less than element @i but greater than element @(i-1)
   * x < xs(i) && x > xs(i-1)
   *
   */
  case class B_LT(i: Int) extends OBinarySearchLocation {
    override def beforeOrAt = i
  }
  /**
   * Index of the first element in the seq that is equal to what
   * we searched for.
   *
   * x == xs(i) && x > xs(i-1)
   *
   *
   */
  case class B_EQ(i: Int) extends OBinarySearchLocation {
    override def beforeOrAt = i
    override def foundAt: Option[Int] = Some(i)
  }
  /**
   * Greater than the last element
   *
   * x > xs(xs.size - 1)
   *
   */
  case class B_TL(sz: Int) extends OBinarySearchLocation {
    override def beforeOrAt = sz
  }
}
