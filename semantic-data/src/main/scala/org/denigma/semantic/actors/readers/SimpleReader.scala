package org.denigma.semantic.actors.readers

import org.denigma.semantic.actors.NamedActor
import akka.actor.Actor
import org.openrdf.model.Value
import org.denigma.semantic.reading.queries.SimpleQueryManager
import org.denigma.semantic.reading.CanRead

/*

 */
//trait SimpleReader {
//  reader:NamedActor with CanRead =>
//
//  def simpleQuery: Actor.Receive = {
//
//    case sel @ SimpleRead.Select(query,offset,limit) if sel.isPaginated=>sender !  qsm.select(query)
//
//    case sel @ SimpleRead.Select(query,offset,limit) if !sel.isPaginated=>sender ! qsm.select(query,offset,limit)
//
//    case SimpleRead.Question(query)=>sender ! qsm.question(query)
//
//    case SimpleRead.Construct(query)=>sender ! qsm.construct(query)
//
//    case SimpleRead.Bind(query,params:Map[String,Value]) =>sender ! qsm.bindedQuery(query,params)
//
//    case SimpleRead.BindPaginated(query,offset,limit,params:Map[String,Value])=>sender ! qsm.bindedQuery(query,params)
//
//
//  }
//
//
//  /*
//  query manager
//   */
//  object qsm extends SimpleQueryManager{
//    val lg = reader.lg
//    def readConnection = reader.readConnection
//  }
//}
