package cgta.serland
package json

import scala.scalajs.js

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/30/14 5:29 PM
//////////////////////////////////////////////////////////////


class JsonStringBuilderImpl extends JsonStringBuilder {
  private val buf = new js.Array[String]()
  override def get(): String = buf.join("")
  override def bool(b: Boolean): JsonStringBuilder = {buf(buf.length) = b.toString; this}
  override def raw(s: String): JsonStringBuilder = {buf(buf.length) = s; this}
  override def dbl(n: Double): JsonStringBuilder = {buf(buf.length) = n.toString; this}
  override def int64(n: Long): JsonStringBuilder = {buf(buf.length) = n.toString; this}
  override def int32(n: Int): JsonStringBuilder = {buf(buf.length) = n.toString; this}
}