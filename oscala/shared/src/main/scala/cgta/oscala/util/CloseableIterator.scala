package cgta.oscala
package util

import java.io.Closeable


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 8/6/15 12:44 PM
//////////////////////////////////////////////////////////////

object CloseableIterator {
  def apply[A](itr: Iterator[A], closeFn: () => Unit): CloseableIterator[A] = new CloseableIterator[A] {
    override def close(): Unit = closeFn()
    override def next(): A = itr.next()
    override def hasNext: Boolean = itr.hasNext
    override def toString() = itr.toString
  }

  def apply[A](itrs: Seq[CloseableIterator[A]]): CloseableIterator[A] = {
    var p: CloseableIterator[A] = null
    def close() {
      p.nullSafe.foreach(_.close())
    }
    CloseableIterator(
      itrs.iterator.flatMap { itr =>
        close()
        p = itr
        itr
      },
      () => close()
    )
  }

}

trait CloseableIterator[+A] extends Iterator[A] with Closeable {

}