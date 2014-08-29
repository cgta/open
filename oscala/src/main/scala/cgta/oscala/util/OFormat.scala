package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 11:31 AM
//////////////////////////////////////////////////////////////


object OFormat {

  object SIPrefixDbls {
    //http://en.wikipedia.org/wiki/Metric_prefix

    val E = 1e18
    val P = 1e15
    val T = 1e12
    val G = 1e9
    val M = 1e6
    val k = 1e3
    val m = 1e-3
    val u = 1e-6
    val n = 1e-9
    val p = 1e-12
    val f = 1e-15
    val a = 1e-18
    val z = 1e-21
    val y = 1e-24

  }

  /**
   * Uses standard si prefixes
   */
  def si(num: Double, showDigits: Int = 4): String = {
    import SIPrefixDbls._
    val absNum = math.abs(num)
    val show = math.max(showDigits, 3)
    if (absNum >= T * 1000) num.toString
    else if (absNum >= T) trimDbl(num / T, show) + "T"
    else if (absNum >= G) trimDbl(num / G, show) + "G"
    else if (absNum >= M) trimDbl(num / M, show) + "M"
    else if (absNum >= k) trimDbl(num / k, show) + "k"
    else if (absNum >= 1) trimDbl(num, show)
    else if (absNum >= m) trimDbl(num / m, show) + "m"
    else if (absNum >= u) trimDbl(num / u, show) + "u"
    else if (absNum >= n) trimDbl(num / n, show) + "n"
    else num.toString
  }

  private def trimDbl(num: Double, showDigits: Int): String = {
    val sb = new StringBuilder()
    val s = num.toString
    val len = s.length

    def loop(i: Int, n: Int) {
      if (i < len && n < showDigits) {
        val c = s(i)
        if (c == '.' || c == ',' || c == '-') {
          sb.append(c)
          loop(i = i + 1, n = n)
        } else if (c == 'E' || c == 'e') {
          //done at the exponent perhaps some day this could start a second 0 pad loop
        } else {
          sb.append(c)
          loop(i = i + 1, n = n + 1)
        }
      }
    }

    loop(0, 0)
    sb.toString()
  }

  def suffixed(num: Double, suffix: String, showDigits: Int): String = {
    trimDbl(num, showDigits) + suffix
  }

  /**
   *
   * Formats a duration that is a number of nanoSeconds
   * as
   * 5d4h2m10s5ms145ms17ns
   *
   * if maxShowFields is set to 2 this would render as
   * 5d4h
   *
   */
  def durSb(nanos: Long, sb: StringBuilder, maxShowFields: Long, baseUnit: String) {
    if (nanos == 0L) {
      sb.append("0" + baseUnit)
    } else {
      var y = nanos
      y = if (nanos < 0L) -y else y
      val ns = y % 1000
      y /= 1000
      val us = y % 1000
      y /= 1000
      val ms = y % 1000
      y /= 1000
      val s = y % 60
      y /= 60
      val m = y % 60
      y /= 60
      val h = y % 24
      y /= 24
      val d = y

      var z = 0

      if (nanos < 0L) {sb.append("-")}
      if (d > 0) {sb.append(d).append("d"); z += 1}
      if (z < maxShowFields && (z > 0 || h > 0)) {sb.append(h).append("h"); z += 1}
      if (z < maxShowFields && (z > 0 || m > 0)) {sb.append(m).append("m"); z += 1}
      if (z < maxShowFields && (z > 0 || s > 0)) {sb.append(s).append("s"); z += 1}
      if (z < maxShowFields && (z > 0 || ms > 0)) {sb.append(ms).append("ms"); z += 1}
      if (z < maxShowFields && (z > 0 || us > 0)) {sb.append(us).append("us"); z += 1}
      if (z < maxShowFields && (z > 0 || ns > 0)) {sb.append(ns).append("ns"); z += 1}
    }
  }

  /**
   * Old school function.
   *
   */
  def durNs(ns: Long, showFields: Long = 2): String = {
    val sb = new StringBuilder
    durSb(ns, sb, showFields, "ns")
    sb.toString()
  }

  def durMs(ms: Long, showFields: Long = 2): String = {
    val sb = new StringBuilder
    durSb(ms * Million, sb, showFields, "ms")
    sb.toString()
  }


}