package cgta.serland

import cgta.serland.backends.{SerJsonOut, SerJsonIn}


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/1/14 3:54 AM
//////////////////////////////////////////////////////////////



object SerlandExportsShared {
  class SerlandTypeAExtensions[A](val x: A) extends AnyVal {
    def toJsonCompact()(implicit ev: SerClass[A]): String = SerJsonOut.toJsonCompact(x)
    def toJsonPretty()(implicit ev: SerClass[A]): String = SerJsonOut.toJsonPretty(x)
  }

  class SerlandStringExtensions(val x: String) extends AnyVal {
    def fromJson[A: SerClass]: A = SerJsonIn.fromJsonString[A](x)
  }
}

/**
 * Mix this into your package object, or whatever you import typically to provide some handy
 * serclass specific implicits
 */
trait SerlandExportsShared {
  implicit def addSerlandTypeAExtensions[A](x : A) = new SerlandExportsShared.SerlandTypeAExtensions[A](x)
  implicit def addSerlandStringExtensions(x : String) = new SerlandExportsShared.SerlandStringExtensions(x)

  def serClass[A : SerClass] = implicitly[SerClass[A]]
  def serSchema[A : SerClass] = implicitly[SerClass[A]].schema
}

//This doesn't work, intellij get's confused and will pick an aribtrary plat here
//trait SerlandExports extends SerlandExportsShared with SerlandExportsPlat