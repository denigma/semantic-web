package org.denigma.semantic.actors.readers

import org.denigma.semantic.actors.{WatchProtocol, NamedActor}
import org.denigma.semantic.reading.CanRead
import akka.actor.Actor
import org.denigma.semantic.actors.readers.protocols.SimpleRead
import org.denigma.semantic.reading._
import scala.util.Try
import org.denigma.semantic.sparql.Pat
import org.openrdf.model.Statement

trait PatternReader {
  me:NamedActor with CanRead =>


  def watchRead:Actor.Receive = {
    case WatchProtocol.PatternRequest(pats) =>
      val result: Try[Map[Pat, Set[Statement]]] =  reader.read{con=>
      pats.map{pat=>pat -> con.getStatements(pat.subjectOrNull, pat.propertyOrNull, pat.objectOrNull, true, pat.contextOrNull).toSet}.toMap
      }
      sender ! result

    case WatchProtocol.TuplePatterns(pats) => lg.error("not implemented yet")

  }

  /**
  Simple query manager (returns internal query results)
    */
  object reader extends DataReader{
    val lg = me.lg
    def readConnection = me.readConnection
  }


}