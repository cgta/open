package cgta.cenum

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/8/14 3:42 PM
//////////////////////////////////////////////////////////////


private[cenum] object CEnumInitStrategyImpl extends CEnumInitStrategy {
  def initOrdinal[A <: CEnum](en: A, el : CEnum#EnumElement): Int = {
    en.setOrdinals()
    el._ord
  }
}