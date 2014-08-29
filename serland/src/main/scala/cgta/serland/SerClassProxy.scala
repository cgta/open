package cgta.serland

import cgta.serland.gen.Gen


//////////////////////////////////////////////////////////////
// Copyright (c) 2010 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ Nov 30, 2010 3:14:57 PM
//////////////////////////////////////////////////////////////



/**
 * This class represents an attempt to overcome circular dependency issues
 * for example when you have a structure something like this:
 * trait NodeOrLeaf
 * case class Node(child : NodeOrLeaf)
 * case class Leaf(x : Int)
 * currently this will lead to stack overflows if you try to define
 * serialization for these classs between the ser for NodeOrLeaf and Node's
 * .ser
 *
 * Used in conjunction with CSerial it looks like this:
 * implicit lazy val ser = proxySerial(forCase(this.apply _))
 *
 */
case class SerClassProxy[A](f: () => SerClass[A]) extends SerClass[A] {
  val lock                 = OLock()
  @volatile var proxied: SerClass[A] = _
  def ensureSet: SerClass[A] = {
    if (proxied == null) {
      lock.using {
        if (proxied == null) {
          proxied = f()
        }
      }
    }
    proxied
  }
  def read(in: SerInput) = ensureSet.read(in)
  def write(a: A, out: SerOutput) = ensureSet.write(a, out)
  //This is done to prevent infinite recursion. however
  //I think it is possible to use schemaRefs to work around
  //this issue. I am waiting for a need. -ben
  def schema = SerSchemas.XUnknown(SerSchemas.XProxy)
  override def gen: Gen[A] = ensureSet.gen
}

