package cgta.oscala
package extensions

import scala.collection.mutable.ArrayBuffer

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:58 PM
//////////////////////////////////////////////////////////////


class SeqExtensions[A](val xs: Seq[A]) extends AnyVal {

  def getOpt(i : Int) : Option[A] = if (i < 0 || i >= xs.length) None else Some(xs(i))


  def toOSeq = xs.toIndexedSeq

  /**
   * Starts at head and gets all elements until the end function returns true
   *
   * @param end function to determine last element to include in the result
   */
  def takeUntil(end: A => Boolean): Seq[A] = {
    val accumulator = ArrayBuffer.empty[A]
    var traverse = xs
    var endIt = false

    while (!(traverse.isEmpty || endIt)) {
      accumulator.append(traverse.head)
      endIt = end(traverse.head)
      traverse = traverse.tail
    }

    accumulator
  }

  /**
   * Removes duplicates from a sequence using a f to provide keys used for determining equality of elements
   * When a duplicate is removed a reducer function r is used to determine what to keep.
   *
   * Warning this class does NOT preserve ordering of the elements!
   *
   * Example (Not That Order was not preserved)
   *
   * xs = List(1->2, 1->3, 1->2, 2->1, 3->4, 4->1, 4-> -1, 5->1)
   * ys = RicherSeq(xs).removeDuplicatesBy(_._1)(List(_,_).maxBy(_._2))
   * ys == Vector((5,1), (1,3), (2,1), (3,4), (4,1))
   *
   * @param f Function that turns an element into some other type B used for equality checks
   * @param r Tie breaker function that chooses which to keep
   * @tparam B
   * @return
   */
  def removeDuplicatesBy[B](f: A => B)(r: (A, A) => A): Seq[A] = {
    xs.toVector.groupBy(f).values.map(_.reduce(r)).toVector
  }

  /**
   * Creates a new Sequence with start before the first element, sep between them, and end after the last element
   *
   * @param start Item to place before the first element
   * @param sep
   * @param end
   * @tparam B
   * @return
   */
  def intersperse[B >: A](start: => Option[B] = None, sep: => Option[B] = None, end: => Option[B] = None): Vector[B] = {
    val buf = new ArrayBuffer[B](xs.size * 2)
    if (start.isDefined) buf += start.get
    val itr = xs.toIterator
    var hasNext = itr.hasNext
    while (hasNext) {
      buf += itr.next
      hasNext = itr.hasNext
      if (hasNext && sep.isDefined) buf += sep.get
    }
    if (end.isDefined) buf += end.get
    buf.toVector
  }
}