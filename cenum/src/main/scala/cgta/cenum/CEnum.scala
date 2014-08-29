package cgta.cenum

import cgta.serland.gen.Gen
import cgta.serland.{SerSchemas, SerClass, SerInput, SerOutput}
import cgta.serland.SerHints.Ser32Hints
import cgta.serland

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Copyright (c) 2010 Ben Jackman, Jeff Gomberg
// Created by bjackman @ 9/21/13 2:43 PM
// Created by bjackman @ 5/8/14 3:38 PM
//////////////////////////////////////////////////////////////

object CEnum {
  import scala.language.experimental.macros

  def getElements[A <: CEnum](enum: A): IVec[A#EET] = macro CEnumMacroImpl.getElementsImpl[A]
  /**
   * This method creates a thread and runs the blk of code passed to it.
   * It is used since constructors in java are not re-entrant accross threads
   * so if two different threads grab two different enum elements very close
   * to each other in time, a deadlock can occur since CEnum.this.getClass
   * will end up calling the constructors of all the member enum elements.
   * And, in this (atleast) one of those constructors will be blocked in
   * another thread on the result of CEnum.this.getClass.
   *
   * Thread1: EnumElementA Base Trait Ctor Begins
   * Thread2: EnumElementB Base Trait Ctor Begins
   * either makes it to the call CEnum.this.getClass
   * add(EnumElementA) get's called, if this is thread1 ok (since the ctor was called there)
   * otherwise this will block since no instance of A is constructed yet. This block will
   * then be a deadlock, since the add cannot complete and hence the CEnum derived class
   * cannot complete construction.
   *
   * This code works around all that by making a separate thread that does initialization.
   * The ordinal method will busy block until the ordinal is set.
   *
   * On ScalaJS this works around issues with parent obj vs contained object initialization order
   *
   */
  val initStrategy: CEnumInitStrategy = CEnumInitStrategyImpl
}

/**
 * Use this class for enumerations.
 * Be sure to call the add method after each element.
 *
 */
trait CEnum {self =>
  /**
   * The base type of the elements in the enumeration.
   * For example for Sides the Base type would be Side
   * and Bid and Ask would extend it.
   *
   * Side should extend the EnumElement trait. And you should
   * add a override type ET = Side in your CEnum.
   *
   */
  type EET <: EnumElement

  object EnumElement {
    //This has to be specifically included for things like .sorted and so on.
    implicit val ordering: Ordering[EET] = new Ordering[EET] {
      def compare(x: EET, y: EET): Int = x.compare(y)
    }
  }

  trait EnumElement extends Ordered[EnumElement] {
    @volatile private[cenum] var _ord = -1

    final def ordinal: Int = {
      if (_ord == -1) {
        CEnum.initStrategy.initOrdinal(CEnum.this, this)
      } else {
        _ord
      }
    }

    //Converts the ordinal to a flag suitable for storage in
    //int-sized bit-sets (classic flag programming pattern)
    final def flag32: Int = 1 << ordinal
    //Converts the ordinal to a flag suitable for storage in
    //long-sized bit sets (classic flag programming pattern)
    final def flag64: Long = 1L << ordinal

    def compare(that: EnumElement): Int = this.ordinal - that.ordinal
  }

  private[cenum] def setOrdinals() {
    elements.zipWithIndex.foreach { case (e, i) =>
      if (e._ord == -1) {
        e._ord = i
      }
    }
  }

  //Needs to be overrided in child class with final override val elements = CEnum.getElements(this)
  def elements: IVec[EET]
  final lazy val toIVec: IVec[EET]         = elements
  final lazy val toIMap: IMap[String, EET] = IMap(toIVec.map(el => el.toString -> el): _*)

  def fromString(s : String) : EET = toIMap(s)

  implicit lazy val ser: SerClass[EET] = new SerClass[EET]() {
    //    override lazy val schema = SerSchemas.Enum(CEnum.this.toIVec.map(_.toString))
    override lazy val schema = {
      import SerSchemas._
      XEnum(XCEnum, toIVec.map(ee => XEnumElement(ee.toString, ee.ordinal)))
    }
    //SerSchemas.Enum(CEnum.this.toIVec.map(_.toString))
    override def gen: Gen[EET] = Gen.oneOf(toIVec)
    override def write(a: EET, out: SerOutput) = {
      if (out.isHumanReadable) {
        out.writeString(a.toString)
      } else {
        out.writeInt32(a.ordinal, hint = Ser32Hints.UVarInt32)
      }
    }
    override def read(in: SerInput): EET = {
      if (in.isHumanReadable()) {
        val s = in.readString()
        toIMap.get(s).getOrElse(serland.READ_ERROR(s"Unknown enum element [$s]"))
      } else {
        val ordinal = in.readInt32(hint = Ser32Hints.UVarInt32)
        val arr = CEnum.this.toIVec
        if (ordinal < 0 || ordinal >= arr.size) {
          serland.READ_ERROR(s"Out of bounds ordinal $ordinal in enum parse")
        } else {
          arr(ordinal)
        }
      }
    }
  }
}