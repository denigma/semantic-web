package org.denigma.semantic.actors

import cutthecrap.utils.striterators.ICloseableIterator
import scala.collection.GenMap

/**
 * Implicit classes for cache
 */
package object cache {

  implicit class CloseableIterator[T](it: ICloseableIterator[T]) extends Iterator[T]{
    override def next(): T = it.next()

    override def hasNext: Boolean = it.hasNext

  }
}
