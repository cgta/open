package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/18/15 5:59 PM
//////////////////////////////////////////////////////////////


object OCache {
  def empty[A, B](maxBuffer: Int): OCache[A, B] = {
    new OCacheImpl[A, B](maxBuffer, OPlatform.getWeakMap[A, B])
  }

  class OCacheImpl[A, B] private[OCache](val maxBuffer: Int, val m: MMap[A, B]) extends OCache[A, B] {
    //val m              = mutable.WeakHashMap.empty[A, B]
    val q              = MQueue.empty[A]
    val unloadByFactor = .2

    override def get(k: A): Option[B] = {
      m.get(k)
    }
    override def getOrSet(k: A, v: => Option[B]): Option[B] = {
      m.get(k) match {
        case Some(v) => Some(v)
        case None =>
          v match {
            case Some(v) =>
              m.put(k, v)
              q.enqueue(k)
              val len = q.length
              if (len > maxBuffer) {
                val dropCount = (len * unloadByFactor).toInt
                (0 until dropCount) foreach { i =>
                  val k = q.dequeue()
                  m.remove(k)
                }
              }
              Some(v)
            case None => None
          }
      }
    }

  }

}


trait OCache[A, B] {
  def get(k: A): Option[B]
  def getOrSet(k: A, v: => Option[B]): Option[B]
}


