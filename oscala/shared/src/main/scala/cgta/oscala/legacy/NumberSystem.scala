package cgta.oscala
package legacy

import scala.annotation.tailrec

/**
 * This trait will allow users to create their own number systems with a configurable "base"
 * But these should be used for counting up from > 0L
 * Negative numbers and fractions / decimal points not supported
 */
//move to cdk.math
trait NumberSystem {
  /**
   * Base of the number system, ie Decimal is base = 10, binary 2, and so forth
   */
  private def base = digitArray.length

  /**
   * Alphabet of digits("chars") that will represent digits in the number system
   * This Array MUST have a length == base
   */
  protected def digitArray: Array[Char]

  /**
   * A map from the digit to the Integer that digit represents in the number system
   */
  private lazy val digitMap: IMap[Char, Int] = IMap() ++ digitArray.zipWithIndex

  private def digitToInt(digit: Char): Option[Int] = digitMap.get(digit)

  //DREAM: - test this version against one which calculates it on the fly with ascii / char values and use fastest
  private def intToDigit(i: Int): Option[Char] = {
    try {
      Some(digitArray(i))
    } catch {
      case e: Throwable => None
    }
  }

  private def stopIt = {
    throw new Exception("Exception in Number System") {
      override def fillInStackTrace() =  this
    }
  }

  /**
   * Converts a based number (represented as a string) into a long if possible
   * Will return None if unable to convert the string into a long
   */
  def baseXToLong(number: String): Option[Long] = {
    if (number.isEmpty) {
      None
    } else {
      try {
        Some(number.map(digitToInt(_).getOrElse {stopIt}).foldLeft(0L)((acc, digit) => acc * base + digit))
      } catch {
        case e: Throwable => None
      }
    }
  }

  /**
   * Converts a decimal long value into a based number (represented as a string)
   * Will return None if try to convert a negative number...
   */
  def longToBaseX(value: Long): Option[String] = {
    @tailrec
    def record(acc: StringBuilder, value: Long): String = {
      value match {
        case x if (x == 0L) => acc.reverse.toString
        case x => {
          acc.append(intToDigit((x % base).toInt).get)
          record(acc, x / base)
        }
      }
    }

    if (value < 0L) {
      None
    } else if (value == 0) {
      intToDigit(0).map(_.toString)
    } else {
      Some(record(new StringBuilder, value))
    }
  }
}

/**
 * Base 32 numbers represented with numbers and uppercase letters
 * Used to create ClOrdIds for the CME (they use those characters)
 */
object Base32Hex extends NumberSystem {
  override protected val digitArray: Array[Char] = {
    val alphabet = ('0' to '9') ++ ('A' to 'V')
    alphabet.toArray[Char]
  }
}


/**
 * Base 36 numbers represented with numbers and uppercase letters
 * Used to create ClOrdIds for the CME (they use those characters)
 */
object Base36 extends NumberSystem {
  override protected val digitArray: Array[Char] = {
    val alphabet = ('0' to '9') ++ ('A' to 'Z')
    alphabet.toArray[Char]
  }
}

/**
 * Base 62 numbers represented with numbers and uppercase + lowercase letters
 * Used for creating forever unique RunIds within the company
 */
object Base62 extends NumberSystem {
  override protected val digitArray: Array[Char] = {
    val alphabet = ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')
    //val WHAT THIS SHOULD BE  = ('0' to '9') ++ ('A' to 'Z') ++ ('a' to 'z')
    alphabet.toArray[Char]
  }
}
