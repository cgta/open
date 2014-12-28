package cgta.serland

import cgta.serland.gen.Gen


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/1/14 10:19 PM
//////////////////////////////////////////////////////////////



trait SerGenable[A] {
  def gen : Gen[A]
  def sample: A = gen.sample.get
}
