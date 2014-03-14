package org.denigma.semantic.actors.cache

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import org.denigma.semantic.cache.UpdateInfo

/**
 * Actor that inserts the cache
 */
class CacheActor extends NamedActor{


  //TODO:
  override def receive: Actor.Receive = {

    case UpdateInfo(transaction,inserted,removed,inferred)=>




  }
}
