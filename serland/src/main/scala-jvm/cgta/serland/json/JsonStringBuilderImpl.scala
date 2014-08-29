package cgta.serland
package json


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/30/14 5:26 PM
//////////////////////////////////////////////////////////////

class JsonStringBuilderImpl extends JsonStringBuilder {
  private val sb = new StringBuilder()
  override def get(): String = sb.toString()
  override def raw(s: String): JsonStringBuilder = { sb.append(s); this }
  override def dbl(n: Double): JsonStringBuilder = { sb.append(n); this }
  override def int64(n: Long): JsonStringBuilder = { sb.append(n); this }
  override def int32(n: Int): JsonStringBuilder = { sb.append(n); this }
  override def bool(b: Boolean): JsonStringBuilder = { sb.append(b); this }
}