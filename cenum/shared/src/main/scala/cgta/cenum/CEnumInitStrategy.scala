package cgta.cenum


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 5/8/14 3:48 PM
//////////////////////////////////////////////////////////////

//See CEnumInitStrategyImpl for sjs vs jvm
private[cenum] trait CEnumInitStrategy {
  def initOrdinal[A <: CEnum](en: A, el : CEnum#EnumElement): Int
}