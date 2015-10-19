package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/24/15 6:09 PM
//////////////////////////////////////////////////////////////

class TraversableOnceExtensions[A](val xs: TraversableOnce[A]) extends AnyVal {

  def minOpt[B >: A](implicit cmp: Ordering[B]): Option[A] = if (xs.isEmpty) None else Some(xs.min[B])
  def maxOpt[B >: A](implicit cmp: Ordering[B]): Option[A] = if (xs.isEmpty) None else Some(xs.max[B])

  def minByOpt[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = if (xs.isEmpty) None else Some(xs.minBy[B](f))
  def maxByOpt[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = if (xs.isEmpty) None else Some(xs.maxBy[B](f))

}