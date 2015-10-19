package cgta.oscala
package util

import scala.collection.IndexedSeqLike


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/14/14 6:44 PM
//////////////////////////////////////////////////////////////

//TODO UNIT TEST THESE CLASSES
object IISeqProxy {
  def apply[A](size: => Int)(f: Int => A) : IISeq[A] = new IISeqProxy(() => size, f)
}

class IISeqProxy[A](val sz: () => Int, val get: Int => A) extends IISeq[A] with IndexedSeqLike[A, IndexedSeq[A]] {
  override def length: Int = sz()
  override def apply(i: Int): A = get(i)
}


object IISeqMerge {
  def apply[Elem, S](xss: IISeq[S], sizeF: S => Int, getF: (S, Int) => Elem): IISeqMerge[Elem, S] = {
    val totalSize = xss.iterator.map(xs => sizeF(xs)).sum
    new IISeqMerge[Elem, S] {
      val startIdxSeq: Array[Int] = {
        var startOffset = 0
        xss.map {xs =>
          val oldStartOffset = startOffset
          startOffset += sizeF(xs)
          oldStartOffset
        }.toArray
      }

      override def underlying: IISeq[S] = xss
      override def length: Int = totalSize
      override def deref[B](i: Int)(f: (S, Int) => B): B = {
        OBinarySearch.floorIndexLast(startIdxSeq, i) match {
          case Some(ssi) => {
            val subIdx = i - startIdxSeq(ssi)
            f(xss(ssi), subIdx)
          }
          case None =>
            throw new IndexOutOfBoundsException("Tried to offset: " + i + " in seq of size: " + totalSize)
        }
      }
      override def apply(i: Int): Elem = {
        deref(i)(getF)
      }

    }
  }
}

trait IISeqMerge[Elem, S] extends IISeq[Elem] with IndexedSeqLike[Elem, IISeq[Elem]] {
  def underlying: IISeq[S]
  def deref[B](i: Int)(f: (S, Int) => B): B
}

/**
 * Used various vector operations
 *
 */
object IISeqOps {
  /**
   * CIISeqOps OWNS the array it is wrapped!
   *
   * DOES NOT COPY THE ARRAY SO BE CAREFUL, CHANGES TO THE UNDERLYING ARRAY
   * WILL DO BAD BAD THINGS.
   *
   */
  def wrapArray[A](xs: Array[A]): IISeq[A] = IISeqProxy[A](xs.size)(i => xs(i))

  /**
   * Merges together multiple indexed seqs
   *
   */
  def merge[A](xss: IISeq[IISeq[A]]): IISeqMerge[A, IISeq[A]] = IISeqMerge(xss, xs => xs.size, (xs, i) => xs(i))


  /**
   *
   * Used for inserting items into sorted vectors, using the index provided by NavigableArrayOps.bs
   *
   */
  def sortedInsert[A](xs: IISeq[A], idx: Int, replaceFn: A => A, defaultValue: => A): (IISeq[A], A) = {
    //Adding a new value
    if (idx < 0) {
      val beforeIdx = -idx - 1
      val x = defaultValue
      if (beforeIdx >= xs.size) {
        (xs :+ x, x)
      } else if (beforeIdx == 0) {
        (xs.+:(x), x)
      } else {
        (xs.patch(beforeIdx, IISeq(x), 0), x)
      }
    } else {
      val x = replaceFn(xs(idx))
      (xs.updated(idx, x), x)
    }
  }

  /**
   * If there is an element at idx (where again it's a NavigableArray.bs style idx)
   * this method will either replace if with replaceFn(xs(idx)) is defined, with that defined value
   * or remove it if replaceFn is not defined there.
   */
  def sortedReplace[A](xs: IISeq[A], idx: Int, replaceFn: A => Option[A]): IISeq[A] = {
    //Adding a new value
    if (idx < 0) {
      //Do nothing
      xs
    } else {
      val x = replaceFn(xs(idx))
      if (x.isDefined) {
        xs.updated(idx, x.get)
      } else {
        xs.patch(idx, IISeq.empty, 1)
      }
    }
  }


}