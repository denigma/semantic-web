package org.denigma.semantic.actors

import akka.actor.Actor
import scala.util.Success
import org.denigma.semantic.storage.RDFStore

/*
Database actor that works with readonly db connection
 */
class DatabaseReader(db:RDFStore) extends DBActor(db)
{



  override def receive: Actor.Receive = {

//    case Data.Read(action)=>
//     sender ! db.read[Any](action)
//
//    case Data.Select(query,action,base)=>
//      sender ! db.selectQuery[Any](query,action)(base)
//
//    case Data.Ask(query,action,base)=>
//      sender ! db.askQuery[Any](query,action)(base)
//
//    case Data.Construct(query,action,base)=>
//      sender ! db.graphQuery[Any](query,action)(base)
//
//    case Data.AnyQuery(query,action,base)=>
//      sender ! db.anyQuery[Any](query,action)(base)


    case v=>
      this.log.error(s"UNKNOWN message received by reader $v")
      //sender ! v
  }
}