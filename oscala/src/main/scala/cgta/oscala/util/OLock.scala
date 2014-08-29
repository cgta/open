package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/27/14 8:25 PM
//////////////////////////////////////////////////////////////

object OLock {
  def apply() : OLock = new OLockImpl
}

trait OLock {
  def lock()
  def unlock()
  def using[A](f : => A) : A = {
    lock()
    try {
      f
    } finally {
      unlock()
    }
  }
}