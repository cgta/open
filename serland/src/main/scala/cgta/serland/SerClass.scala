package cgta.serland


//////////////////////////////////////////////////////////////
// Copyright (c) 2011 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 1/7/11 3:03 PM
//////////////////////////////////////////////////////////////

object SerClass extends SerBasics.SerClasses

trait SerClass[A] extends SerWritable[A] with SerReadable[A] with SerSchemable[A] with SerGenable[A]



