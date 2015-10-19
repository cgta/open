package cgta.serland
package backends

import cgta.serland.json.JsonNodes


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/26/14 5:57 PM
//////////////////////////////////////////////////////////////



object JsonDerefer {
}

//
// DO NOT MAKE THIS A SCALADOC COMMENT OR ELSE THAT WILL MAKE A WARNING (AND BREAK THE BUILD)
// Can be used to resolve {"$ref":"some.path"} magic objects if they are encountered
//
trait JsonDerefer {
  def getNode(jpath: String): JsonNodes.Value
}
