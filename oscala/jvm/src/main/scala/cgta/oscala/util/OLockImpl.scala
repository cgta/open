package cgta.oscala.util

import java.util.concurrent.locks.ReentrantLock


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/27/14 8:30 PM
//////////////////////////////////////////////////////////////


class OLockImpl extends OLock {
  private val theLock = new ReentrantLock()
  override def lock() = theLock.lock()
  override def unlock() = theLock.unlock()
}