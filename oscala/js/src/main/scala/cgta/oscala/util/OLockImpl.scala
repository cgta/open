package cgta.oscala
package util


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/27/14 8:31 PM
//////////////////////////////////////////////////////////////

class OLockImpl extends OLock {
  //In javascript land just treat the lock like a NOOP for now
  override def lock() = {}
  override def unlock() = {}
}