package cgta.serland
package backends



//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/26/11 5:13 PM
//////////////////////////////////////////////////////////////

object SerPenny {

  //Wire Types, has a list of special numbers used to encode various things on the wire
  object WTs {

    lazy val elements = IVec(WStruct, WOneOf(0), WList(0), WOption(0), WSVarInt, WFixed64, WByteArray(0), WFixed8)
    //Does not contain the option type, as it has the same int ordinal as lists
    lazy val elemMap = IMap(elements.filterNot(_.isOption).map(e => e.wtInt -> e): _*)

    trait WT {
      def isOption = false
      def wtInt: Int
    }
    case object WStruct extends WT {
      override def wtInt = 0
    }
    object WOneOf {
      val wtInt = 1
    }
    case class WOneOf(keyId: Int) extends WT {
      override def wtInt = WOneOf.wtInt
    }
    object WList {
      val wtInt = 2
    }
    case class WList(len: Int) extends WT {
      override def wtInt = WList.wtInt
    }
    object WOption {
      val wtInt = WList.wtInt
    }
    //Note that this shares a wt with WList
    case class WOption(len: Int) extends WT {
      override def isOption = true
      override def wtInt = WOption.wtInt
    }
    case object WSVarInt extends WT {
      override def wtInt = 3
    }
    case object WFixed64 extends WT {
      override def wtInt = 4
    }
    object WByteArray {
      val wtInt = 5
    }
    case class WByteArray(len: Int) extends WT {
      override def wtInt = WByteArray.wtInt
    }
    case object WFixed8 extends WT {
      override def wtInt = 6
    }
  }
}
