package cgta.serland
package rpcs

import concurrent.{ExecutionContext, Future}


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 9/9/14 11:09 PM
//////////////////////////////////////////////////////////////


trait RpcRegistration {
  def name : String
  def call(s: String)(implicit ec: ExecutionContext): Future[String]
}

case class RpcRegistration0[R](name: String, f: () => Future[R], serR: SerClass[R]) extends RpcRegistration {
  override def call(s: String)(implicit ec: ExecutionContext): Future[String] = {
    f().map(_.toJsonCompact()(serR))
  }
}
case class RpcRegistration1[T1, R](name: String, f: T1 => Future[R], serT1: SerClass[T1], serR: SerClass[R]) extends RpcRegistration  {
  override def call(s: String)(implicit ec: ExecutionContext): Future[String] = {
    f(s.fromJson(serT1)).map(_.toJsonCompact()(serR))
  }
}

trait Rpcs {
  val baseName: String
  private      var rpcs   = List.empty[RpcRegistration]
  private lazy val rpcMap = Map(rpcs.map(r => r.name -> r): _*)
  def callRpc(name: String, args: String)(implicit ec: ExecutionContext): Future[String] = {
    rpcMap(name).call(args)
  }

  def register[R](name: String, f: () => Future[R])(implicit serR: SerClass[R]) {
    rpcs ::= RpcRegistration0(name, f, serR)
  }
  def register[T1, R](name: String, f: T1 => Future[R])(implicit serT1: SerClass[T1], serR: SerClass[R]) {
    rpcs ::= RpcRegistration1(name, f, serT1, serR)
  }

  def remotely[R](name: String)(implicit serR: SerClass[R]): Future[R]
  def remotely[T1, R](name: String, a: T1)(implicit serT1: SerClass[T1], serR: SerClass[R]): Future[R]

}
