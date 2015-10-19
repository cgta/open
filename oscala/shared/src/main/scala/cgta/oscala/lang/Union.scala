package cgta.oscala
package lang


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 9/10/15 9:42 AM
//////////////////////////////////////////////////////////////
//Copied from @sjrd implementation (similar to scala-js)

object Union extends Union {
  /** Evidence that `A <: B`, taking top-level `|`-types into account. */
  sealed trait Evidence[-A, +B]

  /** A unique instance of `Evidence`. */
  private object ReusableEvidence extends Evidence[scala.Any, scala.Any]

  abstract sealed class EvidenceLowestPrioImplicits {
    /** If `A <: B2`, then `A <: B1 | B2`. */
    implicit def right[A, B1, B2](implicit ev: Evidence[A, B2]): Evidence[A, B1 | B2] =
      ReusableEvidence.asInstanceOf[Evidence[A, B1 | B2]]
  }

  abstract sealed class EvidenceLowPrioImplicits extends EvidenceLowestPrioImplicits {
    /** If `A <: B1`, then `A <: B1 | B2`. */
    implicit def left[A, B1, B2](implicit ev: Evidence[A, B1]): Evidence[A, B1 | B2] =
      ReusableEvidence.asInstanceOf[Evidence[A, B1 | B2]]
  }

  object Evidence extends EvidenceLowPrioImplicits {
    /** `A <: A`. */
    implicit def base[A]: Evidence[A, A] =
      ReusableEvidence.asInstanceOf[Evidence[A, A]]

    /** If `A1 <: B` and `A2 <: B`, then `A1 | A2 <: B`. */
    implicit def allSubtypes[A1, A2, B](
        implicit ev1: Evidence[A1, B], ev2: Evidence[A2, B]): Evidence[A1 | A2, B] =
      ReusableEvidence.asInstanceOf[Evidence[A1 | A2, B]]
  }

  /** Upcast `A` to `B1 | B2`.
   *
   *  This needs evidence that `A <: B1 | B2`.
   */
  implicit def from[A, B1, B2](a: A)(implicit ev: Evidence[A, B1 | B2]): B1 | B2 =
    a.asInstanceOf[B1 | B2]

  /** Operations on union types. */
  implicit class UnionOps[A <: _ | _](val self: A) extends AnyVal {
    /** Explicitly merge a union type to a supertype (which might not be a
     *  union type itself).
     *
     *  This needs evidence that `A <: B`.
     */
    def merge[B](implicit ev: Evidence[A, B]): B =
      self.asInstanceOf[B]
  }
}

sealed abstract class Union {
  /* need to declare this in an class otherwise we get:
   * error: only classes can have declared but undefined members
   */
  type |[A, B]
}