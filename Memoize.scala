package henix.ssoup

import scala.collection.concurrent.TrieMap

/**
 * https://github.com/henix/blog/issues/15
 */
class Memoize1[-T, +R](f: T => R) extends (T => R) {
  private[this] val cache = TrieMap.empty[T, R]
  def apply(x: T): R = cache.getOrElseUpdate(x, f(x))
}

object Memoize1 {
  def apply[T, R](f: T => R) = new Memoize1(f)
}
