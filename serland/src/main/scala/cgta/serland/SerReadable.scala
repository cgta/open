package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 3/1/14 2:39 AM
//////////////////////////////////////////////////////////////


//object SerReadable extends SerBasics.Readable

trait SerReadable[A] {
  def read(in: SerInput): A
}