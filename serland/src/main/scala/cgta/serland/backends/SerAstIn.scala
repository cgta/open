package cgta.serland
package backends

import cgta.serland.SerHints.Ser32Hints.Ser32Hint
import cgta.serland.SerHints.Ser64Hints.Ser64Hint
import cgta.serland.backends.SerAstNodes._

object SerAstIn {
  def fromAst[A: SerClass](ast: SerAstRoot): A = {
    val decoder = new SerAstIn(ast, isHumanReadable = true)
    try {
      serClass[A].read(decoder)
    } catch {
      case e: Throwable =>
        READ_ERROR(s"Unable to parse ast $ast", e)
    }

  }
}

class SerAstIn(root: SerAstRoot, override val isHumanReadable: Boolean) extends SerInput {
  //When this is true then fields should be read in in their human readable versions.
  var stack: List[SerAstNode] = root.child.get :: Nil

  def peekIf[A](f: PartialFunction[SerAstNode, A]): A = {
    if (f.isDefinedAt(stack.head)) {
      f(stack.head)
    } else {
      READ_ERROR(s"stack.head was not of expected type, $stack")
    }
  }

  def peek() = {
    stack.head
  }

  def push(n: SerAstNode) {
    stack ::= n
  }
  def pop() {
    stack = stack.tail
  }

  override def readOneOfBegin(): Either[String, Int] = {
    peek() match {
      case SerAstOneOf(key, id, Some(c)) =>
        val r = if (isHumanReadable) Left(key) else Right(id)
        push(c)
        r
      case SerAstOneOf(key, id, None) =>
        READ_ERROR(s"One of node was empty $stack, $root")
      case x => READ_ERROR(s"Expected oneOf but got $x, $stack, $root")
    }
  }
  override def readOneOfEnd() { pop() }
  override def readStructBegin() {
    peek() match {
      case SerAstStruct(members) =>
      case x =>
        READ_ERROR(s"Expected struct but got $x, $stack, $root")
    }
  }
  override def readStructEnd() {}
  override def readFieldBegin(name: String, id: Int) {
    peek() match {
      case SerAstStruct(members) =>
        members.find(_.name =?= name) match {
          case Some(field) =>
            field.child match {
              case Some(child) =>
                push(child)
              case None =>
                READ_ERROR(s"Missing field value $name $id $stack")
            }
          case None =>
            READ_ERROR(s"Missing field $name $id $stack")
        }
      case x =>
        READ_ERROR(s"Expected struct to be on top of stack but got $x, $stack, $root")
    }
  }
  override def readFieldEnd() { pop() }

  override def readOption[A](sca: SerReadable[A]): Option[A] = {
    peek() match {
      case SerAstOption(Some(child)) =>
        push(child)
        val r = sca.read(this)
        pop()
        Some(r)
      case SerAstOption(None) => None
      case x => READ_ERROR(s"Expected Option to be on top of stack $stack")
    }
  }
  override def readIterable[A](sca: SerReadable[A]): Iterable[A] = {
    peek() match {
      case SerAstIterable(children) =>
        children.map { child =>
          push(child)
          val r = sca.read(this)
          pop()
          r
        }
      case x => READ_ERROR(s"Expected iterable to be on top of stack $stack")
    }
  }

  override def readSerInput(): () => SerInput = {
    val newRoot = stack.head.dup()
    () => new SerAstIn(SerAstRoot(Some(newRoot.dup())), isHumanReadable)
  }

  override def readBoolean(): Boolean = peekIf { case x: SerAstBoolean => x.v}
  override def readByte(): Byte = peekIf { case x: SerAstByte => x.v}
  override def readChar(): Char = peekIf { case x: SerAstChar => x.v}
  override def readInt32(hint: Ser32Hint): Int = peekIf { case x: SerAstInt32 => x.v}
  override def readInt64(hint: Ser64Hint): Long = peekIf { case x: SerAstInt64 => x.v}
  override def readDouble(): Double = peekIf { case x: SerAstDouble => x.v}
  override def readString(): String = peekIf { case x: SerAstString => x.v}
  override def readByteArr(): Array[Byte] = peekIf { case x: SerAstByteArr => x.v}

}