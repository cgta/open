package cgta.oscala
package util

import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 11:54 AM
//////////////////////////////////////////////////////////////

object TestOFormat extends FunSuite {


  ignore("si") {
    def v(e: String, n: Double, show: Int = 4, noSign: Boolean = false) {
      val sign = if (noSign) "" else "-"
      Assert.isEquals(e, OFormat.si(n, show), e, n, show)
      Assert.isEquals(sign + e, OFormat.si(-n, show), e, n, show)
    }

    v("0", 0.0, noSign = true)
    v("0", -0.0, noSign = true)
    v("10", 10)
    v("100", 100)
    v("569", 569)
    v("1k", 1000)
    v("1.1k", 1100)
    v("11k", 11000)
    v("11.1k", 11100)
    v("11.1k", 11100)
    v("10k", 10000)
    v("123.4M", 123456789)
    v("123.4G", 123456789000.0)
    v("123.4T", 123456789000000.0)

    v("100.4m", 0.1004)
    v("100.415m", 0.100415, 6)
    v("10m", 0.01)

    v("100.4u", 0.0001004)
    v("100.41u", 0.000100415, 5)

    //NEED TO FIGURE HOW TO FIX THIS
    //println(0.00001/OFormat.SIPrefixDbls.u)
    //-> 10.000000000000002
    v("10u", 0.00001)
  }



  ignore("durNs + durMs") {
    val minMs = 60 * 1000L
    val minNs = minMs * Million

    val hourMs = 60 * minMs
    val hourNs = 60 * minNs

    val dayMs = 24 * hourMs
    val dayNs = 24 * hourNs


    def v(eNs: String, eMs: String, ns: Long, show: Int = 2, noSignNs: Boolean = false, noSignMs: Boolean = false) {
      val signNs = if (noSignNs) "" else "-"
      val signMs = if (noSignMs) "" else "-"
      Assert.isEquals(eNs, OFormat.durNs(ns, show), ns, show)
      Assert.isEquals(signNs + eNs, OFormat.durNs(-ns, show), ns, show)
      Assert.isEquals(eMs, OFormat.durMs(ns / Million, show), ns / Million, show)
      Assert.isEquals(signMs + eMs, OFormat.durMs(-ns / Million, show), ns / Million, show)
    }
    v("0ns", "0ms", 0, noSignNs = true, noSignMs = true)
    v("10ns", "0ms", 10, noSignMs = true)
    v("100us100ns", "0ms", 100100, noSignMs = true)
    v("1h37m", "1h37m", hourNs + 37 * minNs)
    //Fails with Expected [1ms] to be equal to [1ms0us0ns]
    v("1ms100us100ns", "1ms", 1100100, show = 3, noSignMs = true)
  }

}