package cgta.serland

import cgta.otest.FunSuite
import cgta.serland.backends.SerJsonOut


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 6/4/14 4:13 PM
//////////////////////////////////////////////////////////////

object TestSerJsonPrettyFormat extends FunSuite {

  object Baz {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class Baz(a: Int, bs: List[Int])

  object Bar {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class Bar(h: List[Baz], k: Baz)

  object Foo {implicit val ser = SerBuilder.forCase(this.apply _)}
  case class Foo(x: Int, y: List[Bar], z: Bar)

  test("EnsurePrettyFormat") {
    val jsonStr = """
    |{
    |  "x":1,
    |  "y":[
    |    {
    |      "h":[
    |        {
    |          "a":2,
    |          "bs":[
    |            1,
    |            2
    |          ]
    |        },
    |        {
    |          "a":2,
    |          "bs":[
    |            1,
    |            2
    |          ]
    |        }
    |      ],
    |      "k":{
    |        "a":2,
    |        "bs":[
    |          1,
    |          2
    |        ]
    |      }
    |    },
    |    {
    |      "h":[
    |      ],
    |      "k":{
    |        "a":2,
    |        "bs":[
    |          1,
    |          2
    |        ]
    |      }
    |    }
    |  ],
    |  "z":{
    |    "h":[
    |    ],
    |    "k":{
    |      "a":1,
    |      "bs":[
    |        1
    |      ]
    |    }
    |  }
    |}""".stripMargin.trim

    Assert.isEquals(jsonStr, jsonStr.fromJson[Foo].toJsonPretty())
  }

}