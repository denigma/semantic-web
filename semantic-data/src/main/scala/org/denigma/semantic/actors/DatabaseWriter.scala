package org.denigma.semantic.actors

import org.denigma.semantic.data.RDFStore
import akka.actor.Actor

/*
class that is responsible for writes into database
 */
class DatabaseWriter(db:RDFStore) extends DBActor(db){

  override def receive: Actor.Receive = {

    case  Data.Write(action) =>

      sender ! db.write(action)


    case v=>this.log.debug(s"something received by writer $v")
  }
}
