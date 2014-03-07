package org.denigma.semantic.actors.readers

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import org.openrdf.model.Value
import org.denigma.semantic.reading.queries.SimpleQueryManager
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.sparql
import org.denigma.semantic.actors.readers.protocols.SimpleRead

/**
 * Handles simple quries (with native results)
 */
trait SimpleReader {
  reader:NamedActor with CanRead =>


  def simpleQuery: Actor.Receive = {

    case sel:sparql.SelectQuery => sender ! qsm.select(sel.stringValue)

    case sel @ SimpleRead.Select(query,offset,limit) =>
      if(sel.isPaginated) sender ! qsm.select(query,offset,limit) else sender ! qsm.select(query)


    case SimpleRead.Question(query)=>sender ! qsm.question(query)

    case SimpleRead.Construct(query)=>sender ! qsm.construct(query)

    case q @ SimpleRead.Bind(query,binds,offset,limit) => if(q.isPaginated) sender ! qsm.bindedQuery(query,binds,offset,limit) else sender ! qsm.bindedQuery(query,binds)


  }


  /**
  Simple query manager (returns internal query results)
   */
  object qsm extends SimpleQueryManager{
    val lg = reader.lg
    def readConnection = reader.readConnection
  }
}
