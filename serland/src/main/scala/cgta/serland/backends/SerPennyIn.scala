package cgta.serland
package backends


import cgta.oscala.OPlatform
import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.{Ser64Hints, Ser32Hints}
import cgta.serland.SerHints.Ser64Hints.Ser64Hint
import cgta.oscala.util.{Utf8Help, BinaryHelp}
import BinaryHelp.{ByteArrayInStreamReader, InStreamReader}
//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/26/11 5:07 PM
//////////////////////////////////////////////////////////////

object SerPennyIn {
  def fromByteArray[A: SerClass](bs: Array[Byte]): A = {
    val bais = new ByteArrayInStreamReader(bs)
    val sca = implicitly[SerClass[A]]
    val ins = new SerPennyIn(bais)
    try {
      sca.read(ins)
    } catch {
      case e: Throwable => READ_ERROR("SerPenny Decoding Failure " + ins.debugString, e)
    }
  }

}

/**
 * File format:
 *
 * Structs: WStruct
 * fid 0 is reserved, that is the stop field. so don't use it, ever!
 * foreach field of struct x with fid => field.ordinal, wtBytes => wt(field.type), and encoded=> enc(field.value)
 * ((uvar(fid)<<3) | wtBytes) ~ encoded
 * SVarInt: WSVarInt
 * svar(x)
 * Fixed64: WFixed64
 * fixed64(x)
 * OneOf: WOneOf
 * uvar(keyId) ~ uvar(wt) ~ encoded(x)
 * Strings: WByteArray
 * uvar(len(x)) ~ bytes(x)
 * Lists: WList
 * if len(xs) == 0
 * uvar(0)
 * else
 * uvar(len(xs)) ~ wt(xs.elem) ~ rep1(for( x <- xs) yield encoded(x))
 * Options: WList, same as list but it's length is 0 or 1 only
 *
 */
class SerPennyIn(ins: InStreamReader, override val isHumanReadable: Boolean = false) extends SerInput {
  val IS = BinaryHelp.InStream
  val OS = BinaryHelp.OutStream
  case class SPField(fid: Int, wtInt: Int, cleared: Boolean = false)
  private var structStack: List[Option[SPField]] = Nil
  private var scopeStack : List[ScopeLoc]        = Nil
  private var readFirstWt: Boolean               = false

  private val lastFieldUnknown     = -1
  private val lastFieldMissing     = 0
  private val lastFieldPresent     = 1
  private var lastFieldStatus: Int = lastFieldUnknown

  trait ScopeLoc
  case object InObject extends ScopeLoc
  case class InMonad(subWtInt: Int) extends ScopeLoc
  case class InOneOf(subWtInt: Int) extends ScopeLoc

  //Helpful for when something goes amiss
  def debugString: String = {
    "StructStack " + structStack + " -- ScopeStack " + scopeStack + " -- ReadFirstWireType " + readFirstWt
  }

  override def readStructBegin() {
    doInitialCheck()
    structStack ::= None
    scopeStack ::= InObject
  }
  override def readStructEnd() {
    //Ok the struct is done, so pop the top of the struct stack
    //Also we need to skip ahead to the next stop field!
    skipPastNextStopField()
    scopeStack = scopeStack.tail
    structStack = structStack.tail
  }

  private final def skipPastNextStopField() {
    //We will just ignore fields we encounter at this point
    //Unfortunately we have to continue to do all the apropriate nesting and so forth
    //This will bypass based on wiretype any field in an object
    def bypassOnWt(wt: Int) {
      SerPenny.WTs.elemMap.get(wt) match {
        case Some(wt) => wt match {
          case SerPenny.WTs.WStruct => skipPastNextStopField()
          case SerPenny.WTs.WSVarInt => IS.skipVar(ins)
          case SerPenny.WTs.WFixed8 => IS.skip(1)(ins)
          case SerPenny.WTs.WFixed64 => IS.skip(8)(ins)
          case x: SerPenny.WTs.WOneOf =>
            //bypass key
            IS.skipVar(ins)
            val wt = IS.readUVar(ins).toInt
            bypassOnWt(wt)
          case x: SerPenny.WTs.WList =>
            val len = IS.readUVar(ins).toInt
            if (len > 0) {
              val wt = IS.readUVar(ins).toInt
              def bypassElem(i: Int) {
                if (i < len) {
                  bypassOnWt(wt)
                  bypassElem(i + 1)
                }
              }
            }
          case x: SerPenny.WTs.WByteArray => IS.skip(len = IS.readUVar(ins).toInt)(ins)
        }
        case None => READ_ERROR("BadWireType " + wt)
      }
    }

    def loop() {
      loadField()
      if (curField.get.fid == 0) {
        //At the end of the struct!
      } else {
        //Not there we have to bypass this field and keep going
        bypassOnWt(curField.get.wtInt)
        loop()
      }
    }

    loop()
  }

  override def readFieldBegin(name: String, id: Int) {
    if (id < 0) READ_ERROR(s"Invalid field id [$id] for field [$name]")
    //This better be a field
    if (scopeStack.head == InObject) {
      loadField()
      curField match {
        case Some(spf@field) => {
          if (spf.fid == 0) {
            //Stop field read, no more fields in this object
            lastFieldStatus = lastFieldMissing
          } else if (spf.fid == id + 1) {
            //Proceed using the wt for guidance
            lastFieldStatus = lastFieldPresent
            clearField()
          } else {
            //Doesn't match our fid
            lastFieldStatus = lastFieldMissing
          }
        }
        case None => {
          //No more fields, but this should be unreachable, since the stop field
          READ_ERROR("unreachable")
        }
      }
    } else {
      READ_ERROR("not in object scope")
    }
  }
  override def readFieldEnd() {
    if (structStack.head.isDefined && structStack.head.get.cleared) {
      structStack = None :: structStack.tail
    }
    lastFieldStatus = lastFieldUnknown
  }
  override def readOneOfBegin(): Either[String, Int] = {
    doInitialCheck()
    //Ensure the current wt == oneOf
    //require(curWtInt == SerPenny.WTs.WOneOf.wtInt)
    val keyId = Right(IS.readUVar(ins).toInt - 1)
    val wtOfSub = IS.readUVar(ins).toInt
    scopeStack ::= InOneOf(wtOfSub)
    keyId
  }
  override def readOneOfEnd() {
    //Do nothing?
    scopeStack = scopeStack.tail
  }
  override def readIterable[A](sca: SerReadable[A]): Iterable[A] = {
    doInitialCheck()
    val len = IS.readUVar(ins).toInt
    if (len > 0) {
      //We are going to have to preserve this somehow
      val subWtInt = IS.readUVar(ins).toInt
      scopeStack ::= InMonad(subWtInt)
      val res = for (i <- 0 until len) yield {
        sca.read(this)
      }
      scopeStack = scopeStack.tail
      res
    } else {
      Nil
    }
  }
  override def readOption[A](sca: SerReadable[A]): Option[A] = {
    doInitialCheck()
    //If scopeStack.head == InObject
    if (scopeStack.head == InObject) {
      //First level pass is simply to determine if the field was there or not, therefore the previous method
      //should have take care of this
      val lfs = lastFieldStatus
      lastFieldStatus = lastFieldMissing
      if (lfs == lastFieldPresent) {
        scopeStack ::= InMonad(curWtInt)
        val res = Some(sca.read(this))
        scopeStack = scopeStack.tail
        res
      } else if (lfs == lastFieldMissing) {
        None
      } else {
        READ_ERROR("Unset options should be called after a readFieldBegin")
      }
    } else {
      //In this case we have to check to ensure that we
      val len = IS.readUVar(ins).toInt
      if (len == 0) {
        None
      } else if (len == 1) {
        val subWtInt = IS.readUVar(ins).toInt
        scopeStack ::= InMonad(subWtInt)
        val res = Some(sca.read(this))
        scopeStack = scopeStack.tail
        res
      } else {
        READ_ERROR("option lengths should be 1 or 0 but was " + len)
      }
    }
  }

  override def readBoolean(): Boolean = {
    doInitialCheck()
    IS.readUVar(ins) == 1
  }
  override def readByte(): Byte = {
    doInitialCheck()
    IS.readByte(ins)
  }
  override def readChar(): Char = {
    doInitialCheck()
    IS.readUVar(ins).toChar
  }
  override def readInt32(hint: Ser32Hint): Int = {
    doInitialCheck()
    hint match {
      case Ser32Hints.SVarInt32 => IS.readSVar(ins).toInt
      case Ser32Hints.UVarInt32 => IS.readUVar(ins).toInt
    }
  }
  override def readInt64(hint: Ser64Hint): Long = {
    doInitialCheck()
    hint match {
      case Ser64Hints.SVarInt64 => IS.readSVar(ins)
      case Ser64Hints.UVarInt64 => IS.readUVar(ins)
      case Ser64Hints.Fixed64 => IS.readRawLittleEndian64(ins)
    }
  }
  override def readDouble(): Double = {
//    doInitialCheck()
    readString().toDouble
  }
  override def readString(): String = {
    doInitialCheck()
    Utf8Help.fromBytes(readByteArr())
  }
  override def readByteArr(): Array[Byte] = {
    doInitialCheck()
    IS.readByteArray(IS.readUVar(ins).toInt)(ins)
  }

  override def readSerInput(): () => SerInput = {
    val wt = topWtInt()
    val up = new SerPennyUnparsed(ins, wt)
    up.slurp()
    val bs = up.out.toByteArray
    () => new SerPennyIn(new ByteArrayInStreamReader(bs))
  }

  private def doInitialCheck(): Option[Int] = {
    if (!readFirstWt) {
      readFirstWt = true
      Some(IS.readUVar(ins).toInt)
    } else {
      None
    }
  }

  private def topWtInt(): Int = {
    doInitialCheck() match {
      case Some(wt) => wt
      case None => {
        scopeStack.head match {
          case InObject => structStack.head.get.wtInt
          case InMonad(wt) => wt
          case InOneOf(wt) => wt
        }
      }
    }
  }

  private def curWtInt: Int = {
    if (scopeStack.head == InObject) {
      //We get the curWtInt from the head of the structStack
      structStack.head.get.wtInt
    } else {
      //We need to read it from the stream
      IS.readUVar(ins).toInt
    }
  }
  private def loadField() {
    //very simply read in the fid,wt encoded varInt and crack it
    if (structStack.head.isEmpty) {
      val fid_wt = IS.readUVar(ins)
      val fid = (fid_wt >>> 3).toInt
      val wtInt = (fid_wt & 0x7).toInt
      structStack = Some(SPField(fid, wtInt)) :: structStack.tail
    }
  }
  private def curField: Option[SPField] = {
    structStack.head
  }
  private def clearField() {
    structStack = Some(structStack.head.get.copy(cleared = true)) :: structStack.tail
  }
}
