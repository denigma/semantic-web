package org.denigma.semantic.actors.cache

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import scala.util.{Try, Failure, Success}



/**
 * Actor that inserts the cache
 */
class CacheActor extends NamedActor{
  var consumers = Set.empty[Consumer]

  this.bus.subscribe(self,classOf[Cache.UpdateInfo])


  override def receive: Actor.Receive = {


    case upd @ Cache.UpdateInfo(transaction,inserted,removed,inferred, failed)=>
      if(failed)
        this.log.error(s"CACHE TRANSACTION FAILURE ${upd.transaction}")
      else {
        //this.log.debug(upd.toString)
        this.consumers.foreach(con=>con.updateHandler(upd))
      }

    case Cache.Subscribe(consumer) =>  this.consumers = this.consumers + consumer
      log.debug("consumer received")
      sender ! true

    case Cache.UnSubscribe(consumer) => this.consumers = this.consumers - consumer
      log.debug("consumer received")
      sender ! true

  }

}
