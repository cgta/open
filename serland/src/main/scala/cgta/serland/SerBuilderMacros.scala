package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/3/14 10:58 PM
//////////////////////////////////////////////////////////////
import scala.language.experimental.macros

trait SerBuilderMacros {
  def forCaseClass[A <: Product]: SerClass[A] = macro SerBuilderMacrosImpl.forCaseClass[A]

  def forCase[A <: Product](f: Function1[_, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase1[A]
  def forCase[A <: Product](f: Function2[_, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase2[A]
  def forCase[A <: Product](f: Function3[_, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase3[A]
  def forCase[A <: Product](f: Function4[_, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase4[A]
  def forCase[A <: Product](f: Function5[_, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase5[A]
  def forCase[A <: Product](f: Function6[_, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase6[A]
  def forCase[A <: Product](f: Function7[_, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase7[A]
  def forCase[A <: Product](f: Function8[_, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase8[A]
  def forCase[A <: Product](f: Function9[_, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase9[A]
  def forCase[A <: Product](f: Function10[_, _, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase10[A]
  def forCase[A <: Product](f: Function11[_, _, _, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase11[A]
  def forCase[A <: Product](f: Function12[_, _, _, _, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase12[A]
  def forCase[A <: Product](f: Function13[_, _, _, _, _, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase13[A]
  def forCase[A <: Product](f: Function14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, A]): SerClass[A] = macro SerBuilderMacrosImpl.forCase14[A]
  //Add more here when needed, also add them into the macro impls.


  def forSubs1[A, S1 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs1[A, S1]
  def forSubs2[A, S1 <: A, S2 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs2[A, S1, S2]
  def forSubs3[A, S1 <: A, S2 <: A, S3 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs3[A, S1, S2, S3]
  def forSubs4[A, S1 <: A, S2 <: A, S3 <: A, S4 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs4[A, S1, S2, S3, S4]
  def forSubs5[A, S1 <: A, S2 <: A, S3 <: A, S4 <: A, S5 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs5[A, S1, S2, S3, S4, S5]
  def forSubs6[A, S1 <: A, S2 <: A, S3 <: A, S4 <: A, S5 <: A, S6 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs6[A, S1, S2, S3, S4, S5, S6]
  def forSubs7[A, S1 <: A, S2 <: A, S3 <: A, S4 <: A, S5 <: A, S6 <: A, S7 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs7[A, S1, S2, S3, S4, S5, S6, S7]
  def forSubs8[A, S1 <: A, S2 <: A, S3 <: A, S4 <: A, S5 <: A, S6 <: A, S7 <: A, S8 <: A]: SerClass[A] = macro SerBuilderMacrosImpl.forSubs8[A, S1, S2, S3, S4, S5, S6, S7, S8]
  //Add more here when needed, also add them into the macro impls.
}