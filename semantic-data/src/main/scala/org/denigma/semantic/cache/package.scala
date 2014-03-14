package org.denigma.semantic

import com.bigdata.striterator.IChunkedOrderedIterator
import cutthecrap.utils.striterators.ICloseableIterator

/**
 * Created by antonkulaga on 3/14/14.
 */
package object cache {

  implicit class CloseableIterator[T](it: ICloseableIterator[T]) extends Iterator[T]{
    override def next(): T = it.next()

    override def hasNext: Boolean = it.hasNext
  }


}
