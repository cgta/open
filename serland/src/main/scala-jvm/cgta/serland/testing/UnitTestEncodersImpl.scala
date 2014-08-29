package cgta.serland
package testing

import cgta.serland.backends.{SerBsonIn, SerBsonOut}


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/26/14 5:34 PM
//////////////////////////////////////////////////////////////



object UnitTestEncodersImpl extends UnitTestEncoders {
  override def bson: Option[Tester[Any]] = Some(SerBsonTester)

  object SerBsonTester extends Tester[Any] {
    override def encode[A: SerClass](a: A): Any = {
      SerBsonOut.toAny(a)
    }
    override def decode[A: SerClass](s: Any): A = {
      SerBsonIn.fromAny[A](s)
    }
  }

}