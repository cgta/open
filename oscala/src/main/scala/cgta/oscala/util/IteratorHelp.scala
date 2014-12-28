package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 10/3/14 6:21 PM
//////////////////////////////////////////////////////////////


object IteratorHelp {

  //Return Some(x) until you want the iterator to terminate, at which you should
  //return none
  def fromFunction[A](f: () => Option[A]): Iterator[A] = new Iterator[A] {
    private var buf: Option[A] = None
    private var exhausted      = false
    override def hasNext: Boolean = {
      ensureBuf()
      buf.isDefined
    }

    private def ensureBuf() {
      if (!exhausted && buf.isEmpty) {
        val r  = f()
        if (r.isEmpty) exhausted = true
        buf = r
      }
    }
    override def next(): A =  {
      ensureBuf()
      val r = buf.getOrElse(throw new NoSuchElementException("Past End of Iterator"))
      buf = None
      r
    }
  }

}