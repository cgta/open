package cgta.oscala
package util.debugging

import cgta.oscala.util.OFormat


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 6/19/14 6:15 PM
//////////////////////////////////////////////////////////////


object TIMED {
  val squelch = true

  private val lock         = OLock()
  private val eventToCpuNs = MMap.empty[String, Long]

  def apply[A](eventId: String)(f: => A): A = {
    try {
      begin(eventId)
      f
    } finally {
      end(eventId)
    }
  }

  def begin(eventId: String) {
    lock.using(
      //Strategy is to just ignore duplicate events
      if (!eventToCpuNs.contains(eventId)) {
        eventToCpuNs(eventId) = System.nanoTime()
      }
    )
  }

  def end(eventId: String) {
    lock.using(
      //Strategy is to just ignore duplicate events
      if (eventToCpuNs.contains(eventId)) {
        eventToCpuNs.remove(eventId).foreach { startCpuNs =>
          val now = System.nanoTime()
          if (!squelch) {
            println(eventId + ": " + OFormat.durNs(now - startCpuNs))
          }
        }
      }
    )
  }

}