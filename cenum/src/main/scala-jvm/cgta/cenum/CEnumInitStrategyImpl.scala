package cgta.cenum


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/8/14 3:42 PM
//////////////////////////////////////////////////////////////


private[cenum] object CEnumInitStrategyImpl extends CEnumInitStrategy {

  private def onOtherThread(blk: => Unit) {
    val t = new Thread(new Runnable {
      def run() {
        blk
      }
    })
    t.setName("CEnum Init Thread")
    t.setDaemon(true)
    t.start()
  }

  def initOrdinal[A <: CEnum](en: A, el: CEnum#EnumElement): Int = {
    onOtherThread {
      en.setOrdinals()
    }

    while (el._ord == -1) {
      Thread.sleep(1)
    }
    el._ord
  }
}