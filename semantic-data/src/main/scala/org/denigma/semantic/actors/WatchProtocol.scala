package org.denigma.semantic.actors

import org.denigma.semantic.sparql.{Quad, Pat}
import org.openrdf.model.Statement

/**
 * Protocol for watchers
 */
object WatchProtocol{
  
  case class TuplePatterns(patterns:Set[Pat])

  case class PatternRequest(patterns:Set[Pat])

  case class PatternResult(results:Map[Pat,Set[Statement]])

}

