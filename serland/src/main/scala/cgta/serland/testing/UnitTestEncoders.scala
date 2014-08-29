package cgta.serland
package testing

import cgta.serland.SerClass
import cgta.serland.backends.{SerJsonOut, SerJsonIn, SerPennyIn, SerPennyOut}
import cgta.otest.Asserts


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/26/14 5:24 PM
//////////////////////////////////////////////////////////////

trait UnitTestEncoders {
  trait Tester[EncT] {
    def encode[A: SerClass](a: A): EncT
    def decode[A: SerClass](s: EncT): A
    /**
     * Encodes then decodes a value.
     */
    def encodeDecode[A: SerClass, B: SerClass](a: A): B = {
      decode[B](encode[A](a))
    }


    def ensure[A: SerClass](a: A) {
      try {
        val doubled = encodeDecode[A, A](a)
        Asserts.isEquals(a, doubled)
      } catch {
        case e: Throwable => throw new RuntimeException(a.toString, e)
      }
    }
  }

  def all: List[Tester[_]] = List(Some(penny), Some(json), bson).flatten

  def penny = SerPennyTester
  def json = SerJsonTester
  def bson: Option[Tester[Any]] = None

  object SerPennyTester extends Tester[Array[Byte]] {
    override def encode[A: SerClass](a: A): Array[Byte] = {
      SerPennyOut.toByteArray(a)
    }
    override def decode[A: SerClass](s: Array[Byte]): A = {
      SerPennyIn.fromByteArray(s)
    }
  }

  object SerJsonTester extends Tester[String] {
    override def encode[A: SerClass](a: A): String = {
      SerJsonOut.toJsonCompact(a)
    }
    override def decode[A: SerClass](s: String): A = {
      SerJsonIn.fromJsonString[A](s)
    }
  }

}

