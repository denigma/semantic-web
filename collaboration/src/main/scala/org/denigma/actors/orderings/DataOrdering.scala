package org.denigma.actors.orderings

import org.denigma.actors.messages.DataLike

/**
 * Used to order data in sets
 */
class DataOrdering[T<:DataLike] extends Ordering [T]
{
  def compare(x: T, y: T): Int = (x.date.getTime - y.date.getTime).toInt
}
