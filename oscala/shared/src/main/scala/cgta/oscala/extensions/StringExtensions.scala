package cgta.oscala
package extensions

import java.io.File
import scala.annotation.tailrec

import cgta.oscala.util.Utf8Help

import scala.annotation.tailrec


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:58 PM
//////////////////////////////////////////////////////////////


class StringExtensions(val s: String) extends AnyVal {
  def isNumeric = OPlatform.StringUtils.isNumeric(s)
  def wrapped = "[" + s + "]"
  def toFile: File = new File(s)
  def getBytesUTF8: Array[Byte] = Utf8Help.toBytes(s)
  //Like strip margin but doesn't require a margin character
  //Ignore the first line if it's just a newline character
  //also will use the smallest amount of initial spaces
  //(ignoring any line that is all spaces)
  //as the size of the margin that is stripped off each line.
  def stripAuto: String = {
    def minMargin(ss: List[String]): Int = ss.filter(_.trim.nonEmpty).map(_.takeWhile(_ == ' ').size).min

    val sb = new StringBuilder()

    @tailrec
    def loop(ss: List[String], stripCnt: Int, firstLine: Boolean): String = {
      ss match {
        case Nil => sb.toString()
        case s :: Nil =>
          if (s.trim.nonEmpty) {
            if (!firstLine) {sb.append("\n")}
            sb.append(s.drop(stripCnt))
          }
          sb.toString()
        case s :: ss =>
          if (!firstLine) {sb.append("\n")}
          sb.append(s.drop(stripCnt))
          loop(ss, stripCnt, firstLine = false)
      }
    }

    s.split("\\n").toList match {
      case Nil => ""
      case "" :: ss => loop(ss, minMargin(ss), firstLine = true)
      case ss => loop(ss, minMargin(ss), firstLine = true)
    }
  }


  def removeEnding(ending: String): String = {
    if (s.endsWith(ending)) {
      s.substring(0, s.length - ending.length)
    } else {
      s
    }
  }

  def interpolate(xs: (String, String)*): String = {
    xs.foldLeft(s) { (s, k_v) =>
      val (k, v) = k_v
      s.replaceAll(s"%$k%", v)
    }
  }

  def toDoubleOpt = try {Some(s.toDouble)} catch {case e: NumberFormatException => None}
  def toIntOpt = try {Some(s.toInt)} catch {case e: NumberFormatException => None}
  def toLongOpt = try {Some(s.toLong)} catch {case e: NumberFormatException => None}


}