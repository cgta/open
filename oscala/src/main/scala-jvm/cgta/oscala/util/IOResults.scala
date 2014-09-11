package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/7/14 7:09 PM
//////////////////////////////////////////////////////////////

object IOResults {
  sealed trait IOResult[+A] {
    def isEOF = false
    def isLine = false
    def map[B](f : A => B) : IOResult[B]
  }
  case object EOF extends IOResult[Nothing] {
    override def isEOF = true
    override def map[B](f: (Nothing) => B): IOResult[B] = EOF
  }
  case class IOData[A](s: A) extends IOResult[A] {
    override def isLine = true
    override def map[B](f: (A) => B): IOResult[B] = IOData(f(s))
  }
}