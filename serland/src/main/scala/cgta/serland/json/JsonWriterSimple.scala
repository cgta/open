package cgta.serland
package json

import scala.annotation.switch


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/26/14 6:15 PM
//////////////////////////////////////////////////////////////


//TODO Replace with array join version for javascript
object JsonStringBuilder {
  def apply(): JsonStringBuilder = new JsonStringBuilderImpl
}

trait JsonStringBuilder {
  def bool(b: Boolean): JsonStringBuilder
  def int32(n: Int): JsonStringBuilder
  def int64(n: Long): JsonStringBuilder
  def dbl(n: Double): JsonStringBuilder
  def raw(s: String): JsonStringBuilder
  def escaped(s: String): JsonStringBuilder = {
    var i = 0
    while (i < s.length) {
      (s.charAt(i): @switch) match {
        case '\\' => raw("\\\\")
        case '"' => raw("\\\"")
        case '/' => raw("\\/")
        case '\b' => raw("\\b")
        case '\t' => raw("\\t")
        case '\n' => raw("\\n")
        case '\f' => raw("\\f")
        case '\r' => raw("\\r")
        case c =>
          if (c < ' ') {
            val t = "000" + Integer.toHexString(c)
            raw("\\u" + t.takeRight(4))
          } else {
            raw(c.toString)
          }
      }
      i += 1
    }
    this
  }
  def q = { raw("\""); this }
  def qc = { raw("\":"); this }
  def get(): String
}


object JsonWriterCompact {
  def apply(): JsonWriterCompact = new JsonWriterCompact(JsonStringBuilder())
}

class JsonWriterCompact(val out: JsonStringBuilder) extends JsonWriter {
  private var needsCommaStack = List(false)

  def get(): String = out.get()

  private def push() {
    needsCommaStack ::= false
  }
  private def pop() {
    needsCommaStack = needsCommaStack.tail
  }

  private def setNeedsComma(needs: Boolean = true) {
    needsCommaStack = needs :: needsCommaStack.tail
  }
  private def addCommaIfNeeded() {
    if (needsCommaStack.head) {
      out.raw(",")
      setNeedsComma(needs = false)
    }
  }

  override def writeStringField(k: String, v: String) { writeFieldName(k); writeString(v) }
  override def writeString(s: String) { addCommaIfNeeded(); out.q.escaped(s).q; setNeedsComma() }
  override def writeInt(n: Int) { addCommaIfNeeded(); out.int32(n); setNeedsComma() }
  override def writeLong(n: Long) { addCommaIfNeeded(); out.int64(n); setNeedsComma() }
  override def writeDouble(n: Double) { addCommaIfNeeded(); out.dbl(n); setNeedsComma() }
  override def writeBoolean(b: Boolean) { addCommaIfNeeded(); out.bool(b); setNeedsComma() }

  override def writeFieldName(s: String) = {
    addCommaIfNeeded()
    out.q.escaped(s).qc
  }

  private def writeStartContainer(brace: String) {
    addCommaIfNeeded()
    out.raw(brace)
    push()
  }
  private def writeEndContainer(brace: String) {
    pop()
    out.raw(brace)
    setNeedsComma()
  }
  override def writeStartObject() { writeStartContainer("{") }
  override def writeStartArray() { writeStartContainer("[") }
  override def writeEndObject() { writeEndContainer("}") }
  override def writeEndArray() { writeEndContainer("]") }

}

object JsonWriterPretty {
  def apply(): JsonWriterPretty = new JsonWriterPretty(JsonStringBuilder())
}

class JsonWriterPretty(val out: JsonStringBuilder) extends JsonWriter {
  private val indentStr       = "  "
  private var needsCommaStack = List(false)
  private var depth           = 0
  private var writingField = false
  private var inEmptyContainer = false

  def get(): String = out.get()

  private def push() {
    needsCommaStack ::= false
    depth += 1
  }
  private def pop() {
    needsCommaStack = needsCommaStack.tail
    depth -= 1
  }

  private def indent() {
    var i = 0
    while (i < depth) {
      out.raw(indentStr)
      i += 1
    }
  }

  private def setNeedsComma(needs: Boolean = true) {
    needsCommaStack = needs :: needsCommaStack.tail
  }

  private def addComma_?() {
    if (needsCommaStack.head) {
      out.raw(",\n")
      setNeedsComma(needs = false)
    }
  }

  override def writeStringField(k: String, v: String) { writeFieldName(k); writeString(v) }
  override def writeString(s: String) { startVal(); out.q.escaped(s).q; endVal() }
  override def writeInt(n: Int) { startVal(); out.int32(n); endVal() }
  override def writeLong(n: Long) { startVal(); out.int64(n); endVal() }
  override def writeDouble(n: Double) { startVal(); out.dbl(n); endVal() }
  override def writeBoolean(b: Boolean) { startVal(); out.bool(b); endVal() }

  override def writeFieldName(s: String) = {
    inEmptyContainer = false
    addComma_?()
    indent()
    writingField = true
    out.q.escaped(s).qc
  }

  private def startVal() {
    inEmptyContainer = false
    if (!writingField){
      //Probably in an array
      addComma_?()
      indent()
    }
  }

  private def endVal() {
    writingField = false
    setNeedsComma()
  }

  private def writeStartContainer(brace: String) {
    inEmptyContainer = true
    addComma_?()
    if (!writingField) {
      indent()
    }
    out.raw(brace)
    out.raw("\n")
    push()
    writingField = false
  }
  private def writeEndContainer(brace: String) {
    pop()
    if (!inEmptyContainer) {
      out.raw("\n")
    }
    indent()
    out.raw(brace)
    setNeedsComma()
  }
  override def writeStartObject() { writeStartContainer("{") }
  override def writeStartArray() { writeStartContainer("[") }
  override def writeEndObject() { writeEndContainer("}") }
  override def writeEndArray() { writeEndContainer("]") }

}