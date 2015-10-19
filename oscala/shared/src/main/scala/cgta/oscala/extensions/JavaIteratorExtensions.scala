package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/23/15 2:10 PM
//////////////////////////////////////////////////////////////

class JavaIteratorExtensions[A](val itr : java.util.Iterator[A]) extends AnyVal{
  def toScala : Iterator[A]= new Iterator[A] {
    override def hasNext: Boolean = itr.hasNext
    override def next(): A = itr.next()
  }
}