package cgta.oscala
package extensions



//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/15/13 1:58 PM
//////////////////////////////////////////////////////////////

class IMapExtensions[A, B](val m: IMap[A, B]) extends AnyVal {
  /**
   * Maps one value in an immutable map, replacing it with a new one.
   *
   * @param k the key to update, if no key exists the `None` will be passed to f
   * @param f the function that will change the value to a new value, returning none here removes the value
   * @tparam B1 the LUB of the map value type and the new type
   * @return a new map with a k->f(oldValue) replacing k->oldValue
   **/
  def mapValue[B1 >: B](k: A, f: Option[B] => Option[B1]): IMap[A, B1] = {
    f(m.get(k)) match {
      case Some(v) => m + (k -> v)
      case None => m - k
    }
  }
}


