package cgta.serland


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/2/14 2:04 AM
//////////////////////////////////////////////////////////////


abstract class SerException(reason: String, causedBy: Throwable) extends Exception(reason, causedBy) {
  def this(reason: String) = this(reason, null)
}

class SerReadException(reason: String, causedBy: Throwable) extends SerException(reason, causedBy) {
  def this(reason: String) = this(reason, null)
}
class SerWriteException(reason: String, causedBy: Throwable) extends SerException(reason, causedBy) {
  def this(reason: String) = this(reason, null)
}
