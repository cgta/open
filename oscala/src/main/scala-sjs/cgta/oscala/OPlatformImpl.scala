package cgta.oscala

import scala.scalajs.js

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 8:51 PM
//////////////////////////////////////////////////////////////


trait OPlatformImpl extends OPlatform {
  def parseFloat(x : String) : Double = {
    js.Dynamic.global.parseFloat(x).asInstanceOf[Double]
  }

  override val StringUtils  = new StringUtils {
    override def isNumeric(obj: String): Boolean = {
      //See
      //http://stackoverflow.com/questions/18082/validate-decimal-numbers-in-javascript-isnumeric
      //https://github.com/jquery/jquery/blob/ca0086b55a158d8a4347f94254878d6dc5dd90ed/src/core.js#L215
      // parseFloat NaNs numeric-cast false positives (null|true|false|"")
      // ...but misinterprets leading-number strings, particularly hex literals ("0x...")
      // subtraction forces infinities to NaN
      !js.Array.isArray(obj) && (obj.asInstanceOf[js.Dynamic] - parseFloat(obj).asInstanceOf[js.Dynamic] >= 0.asInstanceOf[js.Dynamic]).asInstanceOf[Boolean]
    }
  }
  override val SortingUtils = new SortingUtils {

    override def unstableInplace[A](xs: Array[A])(implicit ord: Ordering[A]): Unit = {
      val len = xs.length
      val ys = xs.toJsArray.sort((a: A, b: A) => ord.compare(a, b))
      var i = 0
      while (i < len) {
        xs(i) = ys(i)
        i += 1
      }
    }
  }
}