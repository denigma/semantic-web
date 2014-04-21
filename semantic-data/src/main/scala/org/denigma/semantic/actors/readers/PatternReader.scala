package org.denigma.semantic.actors.readers

import org.denigma.semantic.actors.{WatchProtocol, NamedActor}
import org.denigma.semantic.reading.CanRead
import akka.actor.Actor
import org.denigma.semantic.actors.readers.protocols.SimpleRead
import org.denigma.semantic.reading._
import scala.util.Try
import org.openrdf.model.Statement
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.sesame._

import org.scalax.semweb.rdf.Quad
import org.scalax.semweb.sparql.Pat

trait PatternReader {
  me:NamedActor with CanRead =>


  /**
   * reads pattern info for catcher
   * @return
   */
  def patternRead:Actor.Receive = {
    case WatchProtocol.PatternRequest(name,pats) =>
      val result: Try[PatternResult] =  reader.read{con=>
      val map: Map[Pat, Set[Quad]] = pats.map(  pat=>
        pat->con.getStatements(pat.subjectResourceOrNull, pat.predicateIRIOrNull, pat.objectValueOrNull, true,pat.contextResOrNull).map[Quad](st=>st).toSet
      ).toMap
        WatchProtocol.PatternResult(name,map)
      }
      sender ! result

    case WatchProtocol.TuplePatterns(name,pats) => lg.error("not implemented yet")

  }

  /**
  Simple query manager (returns internal query results)
    */
  object reader extends DataReader{
    val lg = me.lg
    def readConnection = me.readConnection
  }


}