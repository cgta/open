package cgta.oscala
package impls

import scala.scalajs.js

//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/27/14 6:57 PM
//////////////////////////////////////////////////////////////



//Try this version in the future
//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Base64_encoding_and_decoding#Appendix.3A_Decode_a_Base64_string_to_Uint8Array_or_ArrayBuffer
object Utf8Converter {

  def toBytes(s: String): Array[Byte] = {
    val out = new js.Array[js.Number]()
    var n = 0

    while (n < s.length) {
      val c = (s: js.prim.String).charCodeAt(n)

      if (c < 128) {
        out.push(c)
      } else if (c < 2048) {
        out.push((c >> 6) | 192)
        out.push((c & 63) | 128)
      } else {
        out.push((c >> 12) | 224)
        out.push(((c >> 6) & 63) | 128)
        out.push((c & 63) | 128)
      }

      n += 1
    }

    def toByte(x: js.Number): Byte = {
      val y: Double = x
      y.toByte
    }

    out.iterator.map(toByte).toArray
  }
  def fromBytes(bytes: Array[Byte]): String = {
    val out = new js.Array[js.String]()
    var pos = 0

    while (pos < bytes.length) {
      val c1 = bytes(pos).toInt & 0xFF
      pos += 1
      if (c1 < 128) {
        out.push(js.Dynamic.global.String.fromCharCode(c1).asInstanceOf[js.String])
      } else if (c1 > 191 && c1 < 224) {
        val c2 = bytes(pos)
        pos += 1
        out.push(js.Dynamic.global.String.fromCharCode((c1 & 31) << 6 | c2 & 63).asInstanceOf[js.String])
      } else {
        val c2 = bytes(pos)
        pos += 1
        val c3 = bytes(pos)
        pos += 1
        out.push(js.Dynamic.global.String.fromCharCode((c1 & 15) << 12 | (c2 & 63) << 6 | c3 & 63).asInstanceOf[js.String])
      }
    }
    out.join("").toString
  }
}