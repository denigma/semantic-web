package org.denigma.semantic.actors.cache

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import scala.util.Success
import akka.actor.FSM.Failure


/**
 * Actor that inserts the cache
 */
class CacheActor extends NamedActor{
  var consumers = Set.empty[Consumer]

  //TODO:
  override def receive: Actor.Receive = {

    case Failure(Cache.UpdateInfo(transaction,inserted,removed,inferred))=>
      this.log.error(s"CACHE TRANSACTION FAILUER $transaction")


    case upd @ Cache.UpdateInfo(transaction,inserted,removed,inferred)=>
      this.consumers.foreach(con=>con.updateHandler(upd))


    case Success(upd:Cache.UpdateInfo)=>
      this.consumers.foreach(con=>con.updateHandler(upd))

    case upd:Cache.UpdateInfo=>
      this.consumers.foreach(con=>con.updateHandler(upd))


    case Cache.Subscribe(consumer) =>  this.consumers = this.consumers + consumer
      sender ! true

    case Cache.UnSubscribe(consumer) => this.consumers = this.consumers - consumer
      sender ! true


  }
}
