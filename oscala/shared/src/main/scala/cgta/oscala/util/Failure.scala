package cgta.oscala
package util

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by kklipsch @ 14/02/14 8:58 AM
//////////////////////////////////////////////////////////////


trait Failure[T[_]] {
  def create[A](e: Throwable): T[A]
  def create[A](t: T[A]): Option[Throwable]
}

trait FailureLike {
  def exception: Option[Throwable]
  def msg: String

  def throwIt = throw throwable
  def throwable = exception.getOrElse(new RuntimeException(msg))
  def errorString = exception.map(_.getMessage).getOrElse(msg)
  def longErrorString = exception.map(ex => (msg +: ex.getStackTrace).mkString("\n")).getOrElse(msg)
}

object Failure {
  def tryIt[T[_] : Failure, A](behavior: => T[A]): T[A] = {
    try {
      behavior
    } catch {
      case x: Throwable => implicitly[Failure[T]].create(x)
    }
  }

  def throwIFFailure[T[_] : Failure, A](t: T[A]) {
    implicitly[Failure[T]].create(t).foreach(x => throw x)
  }
}

