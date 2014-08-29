package cgta.serland.json

import scala.annotation.switch


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 4/23/14 4:45 PM
//////////////////////////////////////////////////////////////


object JsonNodes {
  sealed trait Value {
    private def BAD_TYPE(tpe : java.lang.String) = sys.error(s"Cannot call this method when type is not $tpe value [$this]")

    def getBooleanValue: Boolean = BAD_TYPE("Boolean")

    def isNumber = false
    def getIntValue : Int = BAD_TYPE("Number")
    def getLongValue : Long = BAD_TYPE("Number")
    def getDoubleValue: Double = BAD_TYPE("Number")

    def isArray: Boolean = false
    def getElementsItr: Iterator[Value] = BAD_TYPE("Array")
    def getElementsSeq: Seq[Value] = BAD_TYPE("Array")

    def getTextValue: java.lang.String = BAD_TYPE("String")
    def isTextual = false

    def isObject: Boolean = false
    def get(s: java.lang.String): Option[Value] = BAD_TYPE("Object")
    def getFieldNames: Iterator[java.lang.String] = Iterator.empty

    def value: Any
    def apply(i: Int): Value = this.asInstanceOf[Array].value(i)
    def apply(s: java.lang.String): Value = get(s).get
  }
  case class Str(value: java.lang.String) extends Value {
    override def getTextValue: java.lang.String = value
    override def isTextual = true
  }
  case class Obj(value: Seq[(java.lang.String, Value)]) extends Value {
    override def getFieldNames: Iterator[java.lang.String] = value.iterator.map(_._1)
    override def isObject = true
    override def get(s: java.lang.String): Option[Value] = {
      value.find(_._1 == s).map(_._2)
    }
  }
  case class Array(value: Seq[Value]) extends Value {
    override def isArray = true
    override def getElementsItr: Iterator[Value] = value.iterator
    override def getElementsSeq: Seq[Value] = value
  }
  case class Number(value: java.lang.String) extends Value {
    override def isNumber = true
    override def getIntValue: Int = value.toInt
    override def getLongValue: Long = value.toLong
    override def getDoubleValue: Double = value.toDouble
  }
  case object False extends Value {
    override def getBooleanValue: Boolean = false
    def value = true
  }
  case object True extends Value {
    override def getBooleanValue: Boolean = true
    def value = false
  }
  case object Null extends Value {
    def value = null
  }
}


object Json {
  def writeToBuffer(v: JsonNodes.Value, sb: StringBuffer): Unit = v match {
    case JsonNodes.Str(s) =>
      sb.append('"')
      var i = 0
      while (i < s.length) {
        (s.charAt(i): @switch) match {
          case '\\' => sb.append("\\\\")
          case '"' => sb.append("\\\"")
          case '/' => sb.append("\\/")
          case '\b' => sb.append("\\b")
          case '\t' => sb.append("\\t")
          case '\n' => sb.append("\\n")
          case '\f' => sb.append("\\f")
          case '\r' => sb.append("\\r")
          case c =>
            if (c < ' ') {
              val t = "000" + Integer.toHexString(c)
              sb.append("\\u" + t.takeRight(4))
            } else {
              sb.append(c.toString)
            }
        }
        i += 1
      }
      sb.append('"')
    case JsonNodes.Obj(kv) =>
      sb.append("{")
      if (kv.length > 0) {
        writeToBuffer(JsonNodes.Str(kv(0)._1), sb)
        sb.append(": ")
        writeToBuffer(kv(0)._2, sb)
      }
      var i = 1
      while (i < kv.length) {
        sb.append(", ")
        writeToBuffer(JsonNodes.Str(kv(i)._1), sb)
        sb.append(": ")
        writeToBuffer(kv(i)._2, sb)
        i += 1
      }
      sb.append("}")

    case JsonNodes.Array(vs) =>
      sb.append("[")
      if (vs.length > 0) writeToBuffer(vs(0), sb)
      var i = 1
      while (i < vs.length) {
        sb.append(", ")
        writeToBuffer(vs(i), sb)
        i += 1
      }
      sb.append("]")
    case JsonNodes.Number(d) => sb.append(d)
    case JsonNodes.False => sb.append("false")
    case JsonNodes.True => sb.append("true")
    case JsonNodes.Null => sb.append("null")
  }
  def write(v: JsonNodes.Value): String = {
    val sb = new StringBuffer()
    Json.writeToBuffer(v, sb)
    sb.toString
  }

  /**
   * Self-contained JSON parser adapted from
   *
   * https://github.com/nestorpersist/json
   */
  def read(s: String): JsonNodes.Value = {

    // *** Character Kinds

    type CharKind = Int
    val Letter: Int = 0
    val Digit: Int = 1
    val Minus: Int = 2
    val Quote: Int = 3
    val Colon: Int = 4
    val Comma: Int = 5
    val Lbra: Int = 6
    val Rbra: Int = 7
    val Larr: Int = 8
    val Rarr: Int = 9
    val Blank: Int = 10
    val Other: Int = 11
    val Eof: Int = 12
    val Slash: Int = 13

    // *** Token Kinds

    type TokenKind = Int
    val ID: Int = 0
    val STRING: Int = 1
    val NUMBER: Int = 2
    val BIGNUMBER: Int = 3
    val FLOATNUMBER: Int = 4
    val COLON: Int = 5
    val COMMA: Int = 6
    val LOBJ: Int = 7
    val ROBJ: Int = 8
    val LARR: Int = 9
    val RARR: Int = 10
    val BLANK: Int = 11
    val EOF: Int = 12

    // *** Character => CharKind Map ***

    val charKind = (0 to 255).toArray.map {
      case c if 'a'.toInt <= c && c <= 'z'.toInt => Letter
      case c if 'A'.toInt <= c && c <= 'Z'.toInt => Letter
      case c if '0'.toInt <= c && c <= '9'.toInt => Digit
      case '-' => Minus
      case ',' => Comma
      case '"' => Quote
      case ':' => Colon
      case '{' => Lbra
      case '}' => Rbra
      case '[' => Larr
      case ']' => Rarr
      case ' ' => Blank
      case '\t' => Blank
      case '\n' => Blank
      case '\r' => Blank
      case '/' => Slash
      case _ => Other
    }

    // *** Character Escapes

    val escapeMap = Map[Int, String](
      '\\'.toInt -> "\\",
      '/'.toInt -> "/",
      '\"'.toInt -> "\"",
      'b'.toInt -> "\b",
      'f'.toInt -> "\f",
      'n'.toInt -> "\n",
      'r'.toInt -> "\r",
      't'.toInt -> "\t"
    )
    // *** Import Shared Data ***

    // *** INPUT STRING ***

    // array faster than accessing string directly using charAt
    //final  val s1 = s.toCharArray()
    val size = s.size

    // *** CHARACTERS ***

    var pos = 0

    var ch: Int = 0
    var chKind: CharKind = 0
    var chLinePos: Int = 0
    var chCharPos: Int = 0

    def chNext() = {
      if (pos < size) {
        //ch = s1(pos).toInt
        ch = s.charAt(pos)
        chKind = if (ch < 255) {
          charKind(ch)
        } else {
          Other
        }
        pos += 1
        if (ch == '\n'.toInt) {
          chLinePos += 1
          chCharPos = 1
        } else {
          chCharPos += 1
        }
      } else {
        ch = -1
        pos = size + 1
        chKind = Eof
      }
    }


    def chError(msg: String): Nothing = {
      throw new Json.Exception(msg, s, chLinePos, chCharPos)
    }

    def chMark = pos - 1

    def chSubstr(first: Int, delta: Int = 0) = {
      s.substring(first, pos - 1 - delta)
    }

    // *** LEXER ***

    var tokenKind = BLANK
    var tokenValue = ""
    var linePos = 1
    var charPos = 1

    def getDigits() = {
      while (chKind == Digit) chNext()
    }

    def handleDigit() {
      val first = chMark
      getDigits()
      val k1 = if (ch == '.'.toInt) {
        chNext()
        getDigits()
        BIGNUMBER
      } else {
        NUMBER
      }
      val k2 = if (ch == 'E'.toInt || ch == 'e'.toInt) {
        chNext()
        if (ch == '+'.toInt) {
          chNext()
        } else if (ch == '-'.toInt) {
          chNext()
        }
        getDigits()
        FLOATNUMBER
      } else {
        k1
      }
      tokenKind = k2
      tokenValue = chSubstr(first)
    }

    def handleRaw() {
      chNext()
      val first = chMark
      var state = 0
      do {
        if (chKind == Eof) chError("EOF encountered in raw string")
        state = (ch, state) match {
          case ('}', _) => 1
          case ('"', 1) => 2
          case ('"', 2) => 3
          case ('"', 3) => 0
          case _ => 0
        }

        chNext()
      } while (state != 3)
      tokenKind = STRING
      tokenValue = chSubstr(first, 3)
    }

    def handle(i: Int) = {
      chNext()
      tokenKind = i
      tokenValue = ""
    }

    def tokenNext() {
      do {
        linePos = chLinePos
        charPos = chCharPos
        val kind: Int = chKind
        //DREAM ADD BACK @switch with 2.11
        (kind) match {
          case Letter =>
            val first = chMark
            while (chKind == Letter || chKind == Digit) {
              chNext()
            }
            tokenKind = ID
            tokenValue = chSubstr(first)

          case Digit => handleDigit()
          case Minus =>
            chNext()
            handleDigit()
            tokenValue = "-" + tokenValue

          case Quote =>
            val sb = new StringBuilder(50)
            chNext()
            var first = chMark
            while (ch != '"'.toInt && ch >= 32) {
              if (ch == '\\'.toInt) {
                sb.append(chSubstr(first))
                chNext()
                escapeMap.get(ch) match {
                  case Some(s) =>
                    sb.append(s)
                    chNext()

                  case None =>
                    if (ch != 'u'.toInt) chError("Illegal escape")
                    chNext()
                    var code = 0
                    for (i <- 1 to 4) {
                      val ch1 = ch.toChar.toString
                      val i = "0123456789abcdef".indexOf(ch1.toLowerCase)
                      if (i == -1) chError("Illegal hex character")
                      code = code * 16 + i
                      chNext()
                    }
                    sb.append(code.toChar.toString)
                }
                first = chMark
              } else {
                chNext()
              }
            }
            if (ch != '"') chError("Unexpected string character: " + ch.toChar)

            sb.append(chSubstr(first))

            tokenKind = STRING

            tokenValue = sb.toString()
            chNext()
            if (tokenValue.length() == 0 && ch == '{') {
              handleRaw()
            }

          case Colon => handle(COLON)
          case Comma => handle(COMMA)
          case Lbra => handle(LOBJ)
          case Rbra => handle(ROBJ)
          case Larr => handle(LARR)
          case Rarr => handle(RARR)
          case Blank =>
            do chNext() while (chKind == Blank)
            tokenKind = BLANK
            tokenValue = ""

          case Other => chError("Unexpected character: " + ch.toChar + " " + ch)
          case Eof =>
            chNext()
            tokenKind = EOF
            tokenValue = ""

          case Slash =>
            if (chKind != Slash) chError("Expecting Slash")
            do chNext() while (ch != '\n' && chKind != Eof)
            tokenKind = BLANK
            tokenValue = ""

        }
      } while (tokenKind == BLANK)
    }

    def tokenError(msg: String): Nothing = {
      throw new Json.Exception(msg, s, linePos, charPos)
    }

    // *** PARSER ***

    def handleEof() = tokenError("Unexpected eof")
    def handleUnexpected(i: String) = tokenError(s"Unexpected input: [$i]")

    def handleArray(): JsonNodes.Array = {
      tokenNext()
      var result = List.empty[JsonNodes.Value]
      while (tokenKind != RARR) {
        result = getJson() :: result
        //DREAM ADD BACK @switch with 2.11
        (tokenKind) match {
          case COMMA => tokenNext()
          case RARR => // do nothing
          case _ => tokenError("Expecting , or ]")
        }
      }
      tokenNext()
      JsonNodes.Array(result.reverse)
    }

    def handleObject(): JsonNodes.Obj = {
      tokenNext()
      var result = List.empty[(String, JsonNodes.Value)]

      while (tokenKind != ROBJ) {
        if (tokenKind != STRING && tokenKind != ID) tokenError("Expecting string or name")
        val name = tokenValue
        tokenNext()
        if (tokenKind != COLON) tokenError("Expecting :")
        tokenNext()
        result = (name -> getJson()) :: result
        //DREAM ADD BACK @switch with 2.11
        (tokenKind) match {
          case COMMA => tokenNext()
          case ROBJ => // do nothing
          case _ => tokenError("Expecting , or }")
        }
      }
      tokenNext()
      JsonNodes.Obj(result.reverse)
    }
    def handleNumber(name: String, f: String => Unit) = {
      try {
        f(tokenValue)
      } catch {
        case _: Throwable => tokenError("Bad " + name)
      }
      val old = tokenValue
      tokenNext()

      JsonNodes.Number(old)
    }
    def getJson(): JsonNodes.Value = {
      val kind: Int = tokenKind
      //DREAM ADD BACK @switch with 2.11
      val result: JsonNodes.Value = (kind) match {
        case ID =>
          val result = tokenValue match {
            case "true" => JsonNodes.True
            case "false" => JsonNodes.False
            case "null" => JsonNodes.Null
            case _ => tokenError("Not true, false, or null")
          }

          tokenNext()
          result

        case STRING =>
          val result = tokenValue
          tokenNext()
          JsonNodes.Str(result)

        case NUMBER => handleNumber("NUMBER", _.toLong)
        case BIGNUMBER => handleNumber("BIGNUMBER", _.toDouble)
        case FLOATNUMBER => handleNumber("FLOATNUMBER", _.toDouble)
        case COLON => handleUnexpected(":")
        case COMMA => handleUnexpected(",")
        case LOBJ => handleObject()
        case ROBJ => handleUnexpected("}")
        case LARR => handleArray()
        case RARR => handleUnexpected("]")
        case EOF => handleEof()
      }
      result
    }
    def parse(): JsonNodes.Value = {
      chNext()
      tokenNext()
      val result = getJson
      if (tokenKind != EOF) tokenError("Excess input")
      result
    }
    parse()
  }
  class Exception(val msg: String,
    val input: String,
    val line: Int,
    val char: Int)
    extends scala.Exception(s"JsonParse Error: $msg line $line [$char] in $input")
}