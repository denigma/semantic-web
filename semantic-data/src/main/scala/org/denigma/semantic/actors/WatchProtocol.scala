package org.denigma.semantic.actors

import org.denigma.sparql.Pat
import org.denigma.rdf.Quad

/**
 * Protocol for watchers
 */
object WatchProtocol{
  
  case class TuplePatterns(name:String,patterns:Set[Pat])

  case class PatternRequest(name:String,patterns:Set[Pat])

  case class PatternResult(name:String,results:Map[Pat,Set[Quad]])

}

