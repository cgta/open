package cgta.oscala
package util

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.Promise
import scala.concurrent.duration.Duration


//////////////////////////////////////////////////////////////
// Copyright (c) 2015 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 2/5/15 5:45 PM
//////////////////////////////////////////////////////////////

object Shell {

  case class ExecuteResult(errorCode: Int, stdOut: List[String], stdErr: List[String])
  def execute(workingDir: File, cmd: Array[String]): ExecuteResult = {
    val proc = Runtime.getRuntime.exec(cmd, null, workingDir)
    //Read the buffers, otherwise eventually the process can
    //lock up as it waits for it's output to be read.
    //http://stackoverflow.com/questions/13008526/runtime-getruntime-execcmd-hanging (read answer)
    def gobble(buf: BufferedReader, promise: Promise[List[String]]) {
      val t = new Thread {
        val out = new ListBuffer[String]

        override def run() {
          try {
            def readBuf(buf: BufferedReader) {
              buf.readLine() match {
                case null =>
                  buf.close()
                  promise.success(out.toList)
                case line =>
                  out += line
                  //println(line)
                  readBuf(buf)
              }
            }
            readBuf(buf)
          } catch {
            case e : Throwable => promise.failure(e)
          }
        }
      }
      t.start()
    }

    val stdOut = Promise[List[String]]()
    val stdErr = Promise[List[String]]()
    gobble(new BufferedReader(new InputStreamReader(proc.getInputStream)), stdOut)
    gobble(new BufferedReader(new InputStreamReader(proc.getErrorStream)), stdErr)
    val so = Await.result(stdOut.future, Duration.Inf)
    val er = Await.result(stdErr.future, Duration.Inf)
    proc.waitFor()
    ExecuteResult(proc.exitValue, so, er)
  }

}