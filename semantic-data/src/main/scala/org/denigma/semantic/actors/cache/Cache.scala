package org.denigma.semantic.actors.cache

import org.denigma.rdf._

/**
 * Cache object
 */
object Cache {

  def failed(transaction:String) = UpdateInfo(transaction,Set.empty[Quad],Set.empty[Quad],Set.empty[Quad],failed = true)

  case class UpdateInfo(transaction:String,inserted:Set[Quad],removed:Set[Quad] = Set.empty,inferred:Set[Quad] = Set.empty, failed:Boolean = false)

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
