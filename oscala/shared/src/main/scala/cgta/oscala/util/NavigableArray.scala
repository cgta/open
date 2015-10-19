package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/18/14 4:38 PM
//////////////////////////////////////////////////////////////


object NavigableArray {
  //  implicit def searchable[A: Ordering](seq: Seq[A]) = new {
  //    def search = NavigableArray(seq)
  //  }
  def apply[A: Ordering](f: Int => A, sz: => Int): NavigableArray[A] = NavigableArray(new IndexedSeq[A] {
    override def apply(idx: Int): A = f(idx)
    override def length: Int = sz
  })
}

case class NavigableArray[A: Ordering](xs: Seq[A]) {
  def contains(x: A): Boolean = OBinarySearch.contains(xs, x)
  def findIndex(x: A): Option[Int] = OBinarySearch.findIndex(xs, x)
  def findIndexes(x: A): Iterator[Int] = OBinarySearch.matches(xs, x)
  def floor(x: A): Option[A] = OBinarySearch.floor(xs, x)
  def floorIndex(x: A): Option[Int] = OBinarySearch.floorIndex(xs, x)
  def floorIndexLast(x: A): Option[Int] = OBinarySearch.floorIndexLast(xs, x)
  def ceiling(x: A): Option[A] = OBinarySearch.ceiling(xs, x)
  def ceilingIndex(x: A): Option[Int] = OBinarySearch.ceilingIndex(xs, x)
  def seqRange: String = OBinarySearch.seqRange(xs)
  def seqRangeAround(x: A): String = OBinarySearch.seqRangeAround(xs, x)
}