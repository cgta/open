package cgta.serland
package json

import cgta.oscala.util.debugging.PRINT
import cgta.otest.FunSuite


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 12/16/14 11:21 AM
//////////////////////////////////////////////////////////////

object TestJsonIO extends FunSuite {

  test("numbers are unchanged") {
    Assert.isEquals("500", JsonIO.writeCompact(JsonIO.read("500")))
  }




  test("formatting is correct") {
    Assert.isEquals(sampleString1, JsonIO.writePretty(JsonIO.read(sampleString1)))
    Assert.isEquals(sampleString2, JsonIO.writePretty(JsonIO.read(sampleString2)))
  }


  lazy val sampleString1 = """
  {
    "indOR":[
    ]
  }""".stripAuto


  lazy val sampleString2 = """
  {
    "inverted":false,
    "OR":[
      {
        "inverted":false,
        "indicatorsAND":[
          {
            "key":"betterLeanFn",
            "betterLeanFn":{
              "invert":false,
              "betterLeanFn":{
                "OR":[
                  {
                    "indOR":[
                    ]
                  }
                ]
              }
            }
          }
        ],
        "indOR":[
        ]
      }
    ]
  }""".stripAuto

}

