////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 4/2/14 1:29 PM
////////////////////////////////////////////////////////////////////////////////

package cgta.serland


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved -- test
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 4/2/14 1:29 PM
//////////////////////////////////////////////////////////////

/**
 * Hints that can be used to adjust how backends encode data for performance reasons.
 */
object SerHints {

  object Ser32Hints {
    sealed trait Ser32Hint
    case object UVarInt32 extends Ser32Hint
    case object SVarInt32 extends Ser32Hint
  }

  object Ser64Hints {
    sealed trait Ser64Hint
    case object UVarInt64 extends Ser64Hint
    case object SVarInt64 extends Ser64Hint
    case object Fixed64 extends Ser64Hint
  }
}