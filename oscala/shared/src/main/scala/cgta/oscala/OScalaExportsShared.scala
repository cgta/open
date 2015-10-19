package cgta.oscala

import cgta.oscala.extensions.ArrayBufferExtensions
import cgta.oscala.extensions.ByteArrayExtensions
import cgta.oscala.extensions.IterableExtensions
import cgta.oscala.extensions.JavaIteratorExtensions
import cgta.oscala.extensions.LongExtensions
import cgta.oscala.extensions.OptionExtensions
import cgta.oscala.extensions.ThrowableExtensions
import cgta.oscala.extensions.TraversableOnceExtensions
import cgta.oscala.extensions.{FutureExtensions, DoubleExtensions, IteratorExtensions, BooleanExtensions, ArrayExtensions, IntExtensions, Func1Extensions, SeqExtensions, IMapExtensions, StringExtensions, ByteExtensions, TypeAExtensions}
import cgta.oscala.util.CForMacros
import cgta.oscala.util.ConcHelp

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, ExecutionContext}


//////////////////////////////////////////////////////////////
// Created by bjackman @ 4/23/14 8:57 AM
//////////////////////////////////////////////////////////////


trait OScalaExportsShared extends CForMacros {

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

  val MQueue = scala.collection.mutable.Queue
  type MQueue[A] = scala.collection.mutable.Queue[A]

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
  implicit def addOScalaArrayBufferExtensions[A](a: ArrayBuffer[A]): ArrayBufferExtensions[A] = new ArrayBufferExtensions[A](a)
  implicit def addOScalaArrayExtensions[A](a: Array[A]): ArrayExtensions[A] = new ArrayExtensions[A](a)
  implicit def addOScalaBooleanExtensions[A](a: Boolean): BooleanExtensions = new BooleanExtensions(a)
  implicit def addOScalaByteArrayExtensions(a: Array[Byte]): ByteArrayExtensions = new ByteArrayExtensions(a)
  implicit def addOScalaByteExtensions(a: Byte): ByteExtensions = new ByteExtensions(a)
  implicit def addOScalaDoubleExtensions(a: Double): DoubleExtensions = new DoubleExtensions(a)
  implicit def addOScalaFunc1Extensions[A, B](a: Function[A, B]): Func1Extensions[A, B] = new Func1Extensions[A, B](a)
  implicit def addOScalaFutureExtensions[A](a : Future[A]): FutureExtensions[A] = new FutureExtensions[A](a)
  implicit def addOScalaIMapExtensions[A, B](a: IMap[A, B]): IMapExtensions[A, B] = new IMapExtensions[A, B](a)
  implicit def addOScalaIntExtensions(a: Int): IntExtensions = new IntExtensions(a)
  implicit def addOScalaIterableExtensions[A](a: Iterable[A]): IterableExtensions[A] = new IterableExtensions[A](a)
  implicit def addOScalaTraversableOnceExtensions[A](a: TraversableOnce[A]): TraversableOnceExtensions[A] = new TraversableOnceExtensions[A](a)
  implicit def addOScalaItrExtensions[A](a: Iterator[A]): IteratorExtensions[A] = new IteratorExtensions[A](a)
  implicit def addOScalaJavaItrExtensions[A](a: java.util.Iterator[A]): JavaIteratorExtensions[A] = new JavaIteratorExtensions[A](a)
  implicit def addOScalaLongExtensions(a: Long): LongExtensions = new LongExtensions(a)
  implicit def addOScalaSeqExtensions[A](a: Seq[A]): SeqExtensions[A] = new SeqExtensions[A](a)
  implicit def addOScalaStringExtensions(a: String): StringExtensions = new StringExtensions(a)
  implicit def addOScalaThrowableExtensions(a: Throwable): ThrowableExtensions = new ThrowableExtensions(a)
  implicit def addOScalaTypeAExtensions[A](a: A): TypeAExtensions[A] = new TypeAExtensions[A](a)
  implicit def addOScalaOptionExtensions[A](a: Option[A]): OptionExtensions[A] = new OptionExtensions[A](a)


  def error(msg: String): Nothing = sys.error(msg)
//  def swallowAndThen(fn: => Unit)(implicit handler: Throwable => Unit): Unit = {
//    try {
//      fn
//    } catch {
//      case e: Throwable => handler(e)
//    }
//  }

  def swallow(f: => Any) {
    try {
      f
    } catch {
      case e : Throwable =>
    }
  }

//  def ifNull[@specialized(Byte, Int, Long, Double) A](x : AnyRef, t : A, f : A) : A = if (x == null) t else f

  //Ignores the block
  def noop(f : => Any) {}

  def TODO : Nothing = throw new NotImplementedError(s"TODO: Unfinished code.")
  def TODO(msg : String) : Nothing = throw new NotImplementedError(s"TODO: $msg")

//  def stringFromUTF8Bytes(bytes: Array[Byte]): String = impls.UTF8Impl.fromBytes(bytes)

//  lazy val inScalaJs : Boolean = {
//    try {
//      1 / 0
//      true
//    } catch {
//      case e: ArithmeticException =>
//        false
//    }
//  }

  OPlatform.isScalaJs

  val OLock = util.OLock
  type OLock = util.OLock

  val defaultExecutionContext : ExecutionContext = ConcHelp.defaultExecutionContext
}