package cgta

import java.util.concurrent.locks.Lock

import oscala.{OScalaExportsPlat, OScalaExportsShared}

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/24/13 11:45 PM
//////////////////////////////////////////////////////////////

package object serland extends SerlandExportsShared
with SerlandExportsPlat
with OScalaExportsShared
with OScalaExportsPlat {

  def withLock[A](lock: Lock)(blk: => A) = {
    try {
      lock.lock()
      blk
    } finally {
      lock.unlock()
    }
  }

  def UNSUPPORTED(reason: String) = throw new UnsupportedOperationException(reason)

  def READ_ERROR(reason: String): Nothing = throw new SerReadException(reason)
  def READ_ERROR(reason: String, causedBy: Throwable): Nothing = throw new SerReadException(reason, causedBy)

  def WRITE_ERROR(reason: String): Nothing = throw new SerWriteException(reason)
  def WRITE_ERROR(reason: String, causedBy: Throwable): Nothing = throw new SerWriteException(reason, causedBy)

}
