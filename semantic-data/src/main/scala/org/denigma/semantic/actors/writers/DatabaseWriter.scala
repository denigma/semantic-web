package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.{AkkaLog, NamedActor}
import org.denigma.semantic.writing._
import org.denigma.semantic.commons.LogLike

/*
class that is responsible for writes into database. It does NOT process read queries
 */
class DatabaseWriter(db:CanWrite) extends  NamedActor with Updater{

  override def receive: Actor.Receive = {
    case Update.Update(query:String)=>sender ! this.update(query)

    case v=>
        this.log.debug(s"something received by writer $v")
        sender ! v

  }

  override def lg: LogLike = new AkkaLog(this.log)

  override def writeConnection:WriteConnection= db.writeConnection
}
