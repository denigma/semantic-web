package org.denigma.semantic.actors.readers

import org.denigma.semantic.actors.{AkkaLog, NamedActor}
import akka.actor.Actor
import org.openrdf.model.Value
import org.denigma.semantic.reading.queries.SemanticQueryManager
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.commons.LogLike
import org.denigma.semantic.actors.readers.protocols.Read

/*
trait that quries reader actors and get JSON in response
 */
trait JsReader
{
  /*
  reader actor
   */
  reader:NamedActor with CanRead=>
  
  def jsonQuery: Actor.Receive = {

    /*
    when unspecified query was received
     */
    case q @ Read.Query(query,offset,limit) =>

     if(q.isPaginated)  sender ! qjm.query(query,offset,limit) else sender ! qjm.query(query)

    case sel @ Read.Select(query,offset,limit)=>

      if(sel.isPaginated) sender ! qjm.select(query,offset,limit) else sender ! qjm.select(query)

    case Read.Question(query)=>
      sender ! qjm.question(query)

    case Read.Construct(query)=>
      sender ! qjm.construct(query)

    case q @ Read.Bind(query,binds,offset, limit)=>
      if(q.isPaginated) sender ! qjm.bindedQuery(query,binds,offset,limit) else  sender ! qjm.bindedQuery(query,binds)

//    case q @ Read.Search(query,searches,binds,offset, limit)=>
//      if(q.isPaginated) sender ! qjm.bindedQuery(query,binds,offset,limit) else  sender ! qjm.bindedQuery(query,binds)


  }


  /**
  query manager
   */
  object qjm extends SemanticQueryManager{
    val lg = reader.lg
    def readConnection = reader.readConnection
  }
}
