package cgta.oscala
package util.debugging


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 1/31/15 7:45 PM
//////////////////////////////////////////////////////////////

object TracerObjs {
  class SymbolExtensions(s: Symbol) {
    def ~[A](that: A): A = {
      PRINT | s.name
      that
    }
  }
  class AnyExtensions[A](a: A) {
    def ~(s: Symbol): A = {
      PRINT | s.name
      a
    }
  }

}
trait TracerLow {
  implicit def any2Traceable[A](a: A): TracerObjs.AnyExtensions[A] = new TracerObjs.AnyExtensions[A](a)
}
trait TracerHi extends TracerLow {
  implicit def sym2Traceable(s: Symbol): TracerObjs.SymbolExtensions = new TracerObjs.SymbolExtensions(s)
}
object TRACER extends TracerHi
