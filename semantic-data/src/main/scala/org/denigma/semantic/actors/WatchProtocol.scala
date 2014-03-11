package org.denigma.semantic.actors

import org.denigma.semantic.sparql.Pat
import org.openrdf.model.Statement

/**
 * Protocol for watchers
 */
object WatchProtocol{
  
  case class TuplePatterns(name:String,patterns:Set[Pat])

  case class PatternRequest(name:String,patterns:Set[Pat])

  case class PatternResult(name:String,results:Map[Pat,Set[Statement]])

}

