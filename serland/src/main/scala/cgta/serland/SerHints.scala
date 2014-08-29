////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/2/14 1:29 PM
////////////////////////////////////////////////////////////////////////////////

package cgta.serland


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved -- test
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/2/14 1:29 PM
//////////////////////////////////////////////////////////////

/**
 * Hints that can be used to adjust how backends encode data for performance reasons.
 */
object SerHints {

  object Ser32Hints {
    sealed trait Ser32Hint
    object UVarInt32 extends Ser32Hint
    object SVarInt32 extends Ser32Hint
  }

  object Ser64Hints {
    sealed trait Ser64Hint
    object UVarInt64 extends Ser64Hint
    object SVarInt64 extends Ser64Hint
    object Fixed64 extends Ser64Hint
  }
}