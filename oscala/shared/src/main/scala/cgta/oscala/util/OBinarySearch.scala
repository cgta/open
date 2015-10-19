package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/18/14 4:30 PM
//////////////////////////////////////////////////////////////

object OBinarySearch {

  /**
   * This is a binary search operation
   * if the element is found, and it matches an element at an index precisely,
   * then that index is returned.
   *
   * If the element is not found it will return a negative number.
   * the index returned is as follows, if it is before the 0th index
   * then -1 is returned
   * if it is before the first index, but after the zeroeth
   * then -2 is returned
   * if it is before the second index, but after the first
   * then -3 is returned
   * and so on.
   * if it is after the last index
   * then -(size+1) is returned (as would be expected).
   *
   * Here are some examples
   * xs = [3,5,7,9]
   * Here is a break down of the locations
   * for each input of bs(xs, x)
   * where x is one of:
   * [-Inf,3) = -1
   * [3,3]    = 0
   * (3,5)    = -2
   * [5,5]    = 1
   * (5, 7)   = -3
   * [7,7]    = 2
   * (7,9)    = -4
   * [9,9]    = 3
   * (9, Inf] = -5
   */
  def bs[A: Ordering](a: Seq[A], i: A): Int = {
    val o = implicitly[Ordering[A]]
    var low: Int = 0
    var high: Int = a.size - 1
    while (low <= high) {
      val mid: Int = (low + high) >>> 1
      val midVal: A = a(mid)
      if (o.lt(midVal, i)) low = mid + 1
      else if (o.gt(midVal, i)) high = mid - 1
      else {
        //now since we are allowing duplicates in the arrays
        //and we want to return consistent results
        //we are going to back scan, and keep going until we hit the start
        //of a run, for example on an array of [1,2,5,5,6]
        //This ensures that a search for 5 will always yield
        //index 2 and never index 3 (no matter how many 5's are in there
        //index 2 will be returned, the array still must be sorted
        //for this to work correctly obviously
        var prevIdx = mid - 1
        while (prevIdx >= 0) {
          val prevVal = a(prevIdx)
          if (o.equiv(prevVal, i)) prevIdx -= 1
          else return (prevIdx + 1)
        }
        return 0
      }
    }
    return -(low + 1)
  }

  /**
   * Inserts an element into a sorted array and returns a new sorted array
   * @tparam A type of sequence and element
   * @return new sequence with element inserted in the right spot
   */
  def insert[A: Ordering : Manifest](xs: Array[A], x: A): Array[A] = {
    val searchIndex = bs(xs, x)
    val dst = new Array[A](xs.length + 1)
    val lessLength = if (searchIndex < 0) -searchIndex - 1 else searchIndex
    if (lessLength > 0) Array.copy(xs, 0, dst, 0, lessLength)
    dst(lessLength) = x
    if (lessLength < xs.length) Array.copy(xs, lessLength, dst, lessLength + 1, xs.length - lessLength)
    dst
  }


  //  /**
  //   * Returns the interval such that:
  //   *   y op x is true
  //   *   idxs = [0,1,2,3,4]
  //   *   ys   = [1,2,3,3,4]
  //   *
  //   *   custom(ys, 0, EQ) => empty
  //   *   custom(ys, 0, GTE) => (-1, Inf)
  //   *   custom(ys, 0, GT) => (-1, Inf)
  //   *   custom(ys, 0, LTE) => (-Inf, 0)
  //   *   custom(ys, 0, LT) => (-Inf, 0)
  //   *
  //   *   custom(ys, 1, EQ) => [1]
  //   *   custom(ys, 1, GTE) => [0, Inf)
  //   *   custom(ys, 1, GT) => (0, Inf)
  //   *   custom(ys, 1, LTE) => (-Inf, 1]
  //   *   custom(ys, 1, LT) => (-Inf, 1)
  //   *
  //   *
  //   */
  //  def custom[A: Ordering](ys: Seq[A], op: CmpOps.NotNeq, x: A) = NavigableArrayComplexReturn.fromBs(ys, x, op, bs(ys, x))

  def mappedBs[A, B: Ordering](xs: Seq[A], x: B, f: A => B): Int = {
    val ys = new IndexedSeq[B] {
      def length: Int = xs.length
      def apply(idx: Int): B = f(xs(idx))
    }
    bs(ys, x)
  }

  def findIndex[A: Ordering](a: Seq[A], i: A): Option[Int] = {
    val k: Int = bs(a, i)
    if (k < 0) {
      None
    } else {
      Some(k)
    }
  }

  def contains[A: Ordering](a: Seq[A], i: A): Boolean = {
    bs(a, i) >= 0
  }

  //If  there are multiple repeated values in a row this will skip to the last one
  def lastRepeated[A](xs : Seq[A], i : Int): Int = {
    if (i < xs.length) {
      var n = i
      var done = false
      val xi = xs(i)
      while (n + 1 < xs.length && !done) {
        if (xi == xs(n + 1)) {
          n += 1
        } else {
          done = true
        }
      }
      n
    } else {
      i
    }
  }

  def floor[A: Ordering](a: Seq[A], i: A): Option[A] = {
    floorIndex(a, i) match {
      case None => None
      case Some(index) => Some(a(index))
    }
  }

  def floorIndex[A: Ordering](a: Seq[A], i: A): Option[Int] = {
    val k: Int = bs(a, i)
    if (k < 0) {
      if (k == -1) {
        None
      } else if (-k > a.size) {
        Some(a.size - 1)
      } else {
        Some(-(k + 2))
      }
    } else {
      Some(k)
    }
  }

  /**
   * When there are duplicates this will return the index of
   * the last duplicate
   *
   */
  def floorIndexLast[A: Ordering](xs: Seq[A], x: A): Option[Int] = {
    val o = implicitly[Ordering[A]]
    floorIndex(xs, x).map { idx =>
    //Here we want to use the value that we actually found
      val x = xs(idx)
      def loop(offset: Int): Int = {
        if (idx + offset >= xs.size) offset - 1
        else if (o.equiv(xs(idx + offset), x)) loop(offset + 1)
        else offset - 1
      }
      loop(1) + idx
    }
  }

  def ceiling[A: Ordering](a: Seq[A], i: A): Option[A] = {
    ceilingIndex(a, i) match {
      case None => None
      case Some(index) => Some(a(index))
    }
  }

  def ceilingIndex[A: Ordering](a: Seq[A], i: A): Option[Int] = {
    val k: Int = bs(a, i)
    if (k < 0) {
      if (-k > a.size) {
        None
      } else {
        Some(-(k + 1))
      }
    } else {
      Some(k)
    }
  }

  def matches[A: Ordering](xs: Seq[A], x: A): Iterator[Int] = {
    val i = findIndex(xs, x)
    if (i.isDefined) {
      val h = i.get
      val k = floorIndexLast(xs, x).get
      new Iterator[Int] {
        var c = h
        def next(): Int = {
          val r = c
          c += 1
          r
        }
        def hasNext: Boolean = c <= k
      }
    } else {
      Iterator.empty
    }
  }

  def seqRange[A: Ordering](a: Seq[A]): String = {
    val m = 5
    val f1 = (1 to (m min a.size)).map(i => a(i)).toList.mkString(",")
    val b1 = ((a.size - 1) to (0 max a.size - 1 - m) by -1).map(i => a(i)).toList.reverse.mkString(",")
    f1 + ".." + b1
  }

  def seqRangeAround[A: Ordering](a: Seq[A], i: A): String = {
    val kk = bs(a, i)
    val k = if (kk < 0) -kk else kk
    val m = 10
    val x = k - (m / 2)
    val b1 = (1 to m).map(i => {val z = x + i; z + "[" + a(z) + "]"}).toList.mkString(",")
    b1
  }

}




