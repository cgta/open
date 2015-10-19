package cgta.oscala
package util

//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by kklipsch @ 9/23/13 11:19 AM
//////////////////////////////////////////////////////////////


trait Closer[A] {
  def close(a: A): Unit
}

trait Closable {
  def close()
}

object Closing {
  def apply[A: Closer, B: Closer, C: Closer, T](a: A, b: B, c: C)(blk: (A, B, C) => T): T = {
    Closing(a, b){ (an, bn) =>
      Closing(c){ cn =>
        blk(an, bn, cn)
      }
    }
  }

  def apply[A: Closer, B: Closer, T](a: A, b: B)(blk: (A, B) => T): T = {
    Closing(a) { an =>
      Closing(b) { bn =>
        blk(an, bn)
      }
    }
  }

  def apply[A: Closer, T](a: A)(blk: A => T): T = {
    try {
      blk(a)
    } finally {
      implicitly[Closer[A]].close(a)
    }
  }
}

object Closer {

  implicit def closeable[A <: Closable] = new Closer[A]  {
    def close(a: A) {
      a.close()
    }
  }

  implicit def structuralCloseToCloser[A <: {def close()}] = new Closer[A] {
    def close(a: A) {
      //Enable reflective calls language feature here, structural types use reflection
      //Since it's not anticipated that we'd ever call have to call a close method
      //where this matters this is fine.
      import scala.language.reflectiveCalls
      a.close()
    }
  }

}