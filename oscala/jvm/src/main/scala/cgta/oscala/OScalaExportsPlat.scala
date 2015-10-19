package cgta.oscala

import java.io.File

import cgta.oscala.extensions.FileExtensions


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/16/14 1:05 PM
//////////////////////////////////////////////////////////////

trait OScalaExportsPlat extends OScalaExportsShared {
  implicit def addOScalaJvmFileExtensions(f : File) : FileExtensions = new FileExtensions(f)
}
