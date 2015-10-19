package cgta.oscala
package util

import java.net.{BindException, DatagramSocket}


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 11/30/14 11:17 AM
//////////////////////////////////////////////////////////////

object UnitTestHelp {

  //Typically catch a BindException in case the underlying port fails
  def usingOpenPort(f: Int => Unit) = {
    //Make a new unbound server socket
    val ss = new DatagramSocket(null)
    try {
      //All the socket's port to be shared
      ss.setReuseAddress(true)
      //Bind the socket to an ephemeral port
      ss.bind(null)
      //Get the port that the socket was bound to
      val port = ss.getLocalPort
      //call f with the port
      f(port)
    } finally {
      ss.close()
    }
  }

}