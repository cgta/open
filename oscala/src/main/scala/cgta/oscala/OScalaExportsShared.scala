package cgta.oscala

import cgta.oscala.extensions.ArrayBufferExtensions
import cgta.oscala.extensions.ByteArrayExtensions
import cgta.oscala.extensions.IterableExtensions
import cgta.oscala.extensions.{FutureExtensions, DoubleExtensions, IteratorExtensions, BooleanExtensions, ArrayExtensions, IntExtensions, Func1Extensions, SeqExtensions, IMapExtensions, StringExtensions, ByteExtensions, TypeAExtensions}
import cgta.oscala.util.ConcHelp

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, ExecutionContext}


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/23/14 8:57 AM
//////////////////////////////////////////////////////////////


trait OScalaExportsShared {

  /**
   * Scala type aliases
   */
  val IMap = scala.collection.immutable.Map
  type IMap[K, +V] = scala.collection.immutable.Map[K, V]

  val ISMap = scala.collection.immutable.SortedMap
  type ISMap[K, +V] = scala.collection.immutable.SortedMap[K, V]

  val ISet = scala.collection.immutable.Set
  type ISet[A] = scala.collection.immutable.Set[A]

  val MSet = scala.collection.mutable.Set
  type MSet[A] = scala.collection.mutable.Set[A]

  val ISSet = scala.collection.immutable.SortedSet
  type ISSet[A] = scala.collection.immutable.Set[A]

  val IVec = scala.collection.immutable.Vector
  type IVec[+A] = scala.collection.immutable.Vector[A]

  val IISeq = scala.collection.immutable.IndexedSeq
  type IISeq[+A] = scala.collection.immutable.IndexedSeq[A]

  val MMap = scala.collection.mutable.Map
  type MMap[K, V] = scala.collection.mutable.Map[K, V]

  val OSeq = scala.collection.IndexedSeq
  type OSeq[+A] = scala.collection.IndexedSeq[A]

  type Id[A] = A

  /**
   * Constants
   */
  val Thousand: Long   = 1000
  val Million : Long   = 1000000
  val Billion : Long   = 1000000000
//  val UTF8    : String = "UTF-8"

  /**
   * Extensions
   */
  implicit def addOScalaArrayExtensions[A](a: Array[A]): ArrayExtensions[A] = new ArrayExtensions[A](a)
  implicit def addOScalaByteArrayExtensions(a: Array[Byte]): ByteArrayExtensions = new ByteArrayExtensions(a)
  implicit def addOScalaBooleanExtensions[A](a: Boolean): BooleanExtensions = new BooleanExtensions(a)
  implicit def addOScalaTypeAExtensions[A](a: A): TypeAExtensions[A] = new TypeAExtensions[A](a)
  implicit def addOScalaByteExtensions(a: Byte): ByteExtensions = new ByteExtensions(a)
  implicit def addOScalaIntExtensions(a: Int): IntExtensions = new IntExtensions(a)
  implicit def addOScalaDoubleExtensions(a: Double): DoubleExtensions = new DoubleExtensions(a)
  implicit def addOScalaStringExtensions(a: String): StringExtensions = new StringExtensions(a)
  implicit def addOScalaIMapExtensions[A, B](a: IMap[A, B]): IMapExtensions[A, B] = new IMapExtensions[A, B](a)
  implicit def addOScalaSeqExtensions[A](a: Seq[A]): SeqExtensions[A] = new SeqExtensions[A](a)
  implicit def addOScalaArrayBufferExtensions[A](a: ArrayBuffer[A]): ArrayBufferExtensions[A] = new ArrayBufferExtensions[A](a)
  implicit def addOScalaIterableExtensions[A](a: Iterable[A]): IterableExtensions[A] = new IterableExtensions[A](a)
  implicit def addOScalaItrExtensions[A](a: Iterator[A]): IteratorExtensions[A] = new IteratorExtensions[A](a)
  implicit def addOScalaFunc1Extensions[A, B](a: Function[A, B]): Func1Extensions[A, B] = new Func1Extensions[A, B](a)
  implicit def addOScalaFutureExtensions[A](a : Future[A]): FutureExtensions[A] = new FutureExtensions[A](a)


  def error(msg: String): Nothing = sys.error(msg)

//  def stringFromUTF8Bytes(bytes: Array[Byte]): String = impls.UTF8Impl.fromBytes(bytes)

  lazy val inScalaJs : Boolean = {
    try {
      1 / 0
      true
    } catch {
      case e: ArithmeticException =>
        false
    }
  }

  val OLock = util.OLock
  type OLock = util.OLock

  val defaultExecutionContext : ExecutionContext = ConcHelp.defaultExecutionContext

}