package org.denigma.semantic.actors.cache

import org.denigma.semantic.model.Quad

/**
 * Cache object
 */
object Cache {
  case class UpdateInfo(transaction:String,inserted:Set[Quad],removed:Set[Quad] = Set.empty,inferred:Set[Quad] = Set.empty)

  case class Subscribe(consumer:Consumer)

  case class UnSubscribe(consumer:Consumer)
}




trait UpdateInfoLike{

  def removed: Set[Quad]
  def inserted: Set[Quad]
  def inferred: Set[Quad]

}
trait Consumer{
  def updateHandler(updateInfo:Cache.UpdateInfo)
}
