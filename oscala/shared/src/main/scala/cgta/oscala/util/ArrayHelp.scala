package cgta.oscala
package util

import scala.reflect.ClassTag


//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/24/13 11:19 AM
//////////////////////////////////////////////////////////////


object ArrayHelp {


  /**
   * Adds 
   *
   */
  def setAtGrow[A: ClassTag](arr: Array[A], i: Int, a: A): Array[A] = {
    val xs = if (i >= arr.length) {
      val ys = new Array[A](arr.length * 2)
      Array.copy(arr, 0, ys, 0, arr.length)
      ys
    } else {
      arr
    }
    xs(i) = a
    xs
  }


  /**
   * Removes the item at the specified index, shifting all elements
   * with a higher index to the left,
   * and setting arr(arr.size - 1) = a
   */
  def removeAddOnRight[A](arr: Array[A], idx: Int, a: A): A = {
    if (idx >= 0 && idx < arr.length) {
      var i = idx
      val old = arr(i)
      while (i + 1 < arr.length) {
        arr(i) = arr(i + 1)
        i += 1
      }
      arr(i) = a
      old
    } else {
      throw new IndexOutOfBoundsException("Bad index: " + idx + " Size: " + arr.length)
    }
  }

  /**
   * Moves an element at a location to the end of the array, shifting all other elements to the left
   */
  def moveToEnd[A](arr: Array[A], idx: Int): A = {
    if (idx >= 0 && idx < arr.length) {
      var i = idx
      val old = arr(i)
      while (i + 1 < arr.length) {
        arr(i) = arr(i + 1)
        i += 1
      }
      arr(i) = old
      old
    } else {
      throw new IndexOutOfBoundsException("Bad index: " + idx + " Size: " + arr.length)
    }
  }

  /**
   * Removes the item at the specified index, shifting all elements
   * with a lower index to the right,
   * and  setting arr(0) = a
   */
  def removeAddOnLeft[A](arr: Array[A], idx: Int, a: A): A = {
    if (idx >= 0 && idx < arr.length) {
      var i = idx
      val old = arr(i)
      while (i - 1 >= 0) {
        arr(i) = arr(i - 1)
        i -= 1
      }
      arr(0) = a
      old
    } else {
      throw new IndexOutOfBoundsException("Bad index: " + idx + " Size: " + arr.length)
    }
  }

  //fill the array from i inclusive to u exclusive
  def fill[A](arr: Array[A], start: Int, end: Int, a: A) {
    val e = math.min(arr.length, end)
    def loop(i: Int) {
      if (i < e) {
        arr(i) = a
        loop(i + 1)
      }
    }
    loop(math.max(start, 0))
  }


  /**
   * shifts the array to the left, inserting a's at the end
   */
  def shiftLeft[A](arr: Array[A], amount: Int, a: A) {
    if (amount == 0) {
      //do nothing
    } else if (amount >= arr.length || amount == Int.MinValue) {
      //clear the entire array
      fill(arr, 0, arr.length, a)
    } else if (amount > 0) {
      //shift the array over by the correct amount
      Array.copy(arr, amount, arr, 0, arr.length - amount)
      fill(arr, arr.length - amount, arr.length, a)
    } else {
      //(amount < 0)
      shiftRight(arr, -amount, a)
    }
  }

  /**
   * shifts the array to the right, inserting a's at the start
   */
  def shiftRight[A](arr: Array[A], amount: Int, a: A) {
    if (amount == 0) {
      //do nothing
    } else if (amount >= arr.length || amount == Int.MinValue) {
      //clear the entire array
      fill(arr, 0, arr.length, a)
    } else if (amount > 0) {
      //shift the array over by the correct amount
      Array.copy(arr, 0, arr, amount, arr.length - amount)
      fill(arr, 0, amount, a)
    } else {
      //(amount < 0)
      shiftLeft(arr, -amount, a)
    }
  }

  /**
   * inserts an item at and index shifting what is currently
   * at that index and beyond to the right, returning the
   * last element in the array
   *
   */
  def insertAt[A](arr: Array[A], idx: Int, a: A): A = {
    if (idx >= 0 && idx < arr.length) {
      var i = arr.length - 1
      val old = arr(i)
      while (i > idx) {
        arr(i) = arr(i - 1)
        i -= 1
      }
      arr(idx) = a
      old
    } else {
      throw new IndexOutOfBoundsException("Bad index: " + idx + " Size: " + arr.length)
    }
  }

  /**
   * inserts an item at the specified index, shifting whatever was
   * at that index and before to the left, returning the first
   * element in the array
   *
   * [a,b,c,d], e, 0 -> [e,b,c,d], a
   * [a,b,c,d], e, 3 -> [b,c,d,e], a
   */
  def insertAtShiftLeft[A](arr: Array[A], idx: Int, a: A): A = {
    if (idx >= 0 && idx < arr.length) {
      var i = 0
      val old = arr(i)
      while (i < idx) {
        arr(i) = arr(i + 1)
        i += 1
      }
      arr(idx) = a
      old
    } else {
      throw new IndexOutOfBoundsException("Bad index: " + idx + " Size: " + arr.length)
    }
  }

  def append[A: ClassTag](arr: Array[A], x: A): Array[A] = {
    val xs = new Array[A](arr.length + 1)
    Array.copy(arr, 0, xs, 0, arr.length)
    xs(arr.length) = x
    xs
  }
  def swap[A](arr: Array[A], h: Int, i: Int) {
    val ai = arr(i)
    val ah = arr(h)
    arr(i) = ah
    arr(h) = ai
  }

  def growWithFill[A: ClassTag](src: Array[A], sz: Int)(a: => A): Array[A] = {
    val dst = Array.fill[A](sz)(a)
    Array.copy(src, 0, dst, 0, src.size)
    dst
  }

  def grow[A: ClassTag](src: Array[A], sz: Int): Array[A] = {
    val dst = new Array[A](sz)
    Array.copy(src, 0, dst, 0, src.size)
    dst
  }

  def copyNew[A: ClassTag](arr: Array[A]) = {
    val res = new Array[A](arr.length)
    Array.copy(arr, 0, res, 0, arr.length)
    res
  }

}
