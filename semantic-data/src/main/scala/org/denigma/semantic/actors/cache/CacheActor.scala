package org.denigma.semantic.actors.cache

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import scala.util.{Try, Failure, Success}



/**
 * Actor that inserts the cache
 */
class CacheActor extends NamedActor{
  var consumers = Set.empty[Consumer]


  override def preStart() = {
    super.preStart()
    this.bus.subscribe(self,classOf[Try[Cache.UpdateInfo]])
  }

  //TODO:
  override def receive: Actor.Receive = {


    case upd @ Cache.UpdateInfo(transaction,inserted,removed,inferred)=>
      this.log.debug(upd.toString)
      this.consumers.foreach(con=>con.updateHandler(upd))


    case Success(upd:Cache.UpdateInfo)=>
      this.log.debug(upd.toString)
      this.consumers.foreach(con=>con.updateHandler(upd))

    case Failure(f)=>
      this.log.error(s"CACHE TRANSACTION FAILURE ${f.toString}")

    case Cache.Subscribe(consumer) =>  this.consumers = this.consumers + consumer
      sender ! true

    case Cache.UnSubscribe(consumer) => this.consumers = this.consumers - consumer
      sender ! true

  }

}
