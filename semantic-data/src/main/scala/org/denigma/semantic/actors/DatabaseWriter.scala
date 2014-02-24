package org.denigma.semantic.actors

import akka.actor.Actor
import org.denigma.semantic.storage.RDFStore

/*
class that is responsible for writes into database. It does NOT process read queries
 */
class DatabaseWriter(db:RDFStore) extends DBActor(db){

  override def receive: Actor.Receive = {

//
//   case  Data.Write(action) =>
//
//      sender ! db.readWrite[Any](action)
//
//   case Data.Update(query,action,base) =>
//      sender ! db.update[Any](query,action)(base)

   case v=>
        this.log.debug(s"something received by writer $v")
        sender ! v


  }
}
