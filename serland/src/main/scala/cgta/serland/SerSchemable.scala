package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/2/14 1:42 AM
//////////////////////////////////////////////////////////////


trait SerSchemable[A] {
  def schema : SerSchema
}