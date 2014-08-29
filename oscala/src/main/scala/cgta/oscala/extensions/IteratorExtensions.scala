package cgta.oscala
package extensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/14/14 8:17 PM
//////////////////////////////////////////////////////////////

class IteratorExtensions[A](val itr: Iterator[A]) extends AnyVal {
  def nextOption() : Option[A] = if (itr.hasNext) Some(itr.next()) else None

}