package org.denigma.semantic.cache

import org.denigma.semantic.sparql.Pat
import org.openrdf.model.Statement
import org.denigma.semantic.controllers.{WithSemanticReader, QueryController}
import org.denigma.semantic.actors.WatchProtocol.{PatternRequest, PatternResult}
import org.denigma.semantic.actors.WatchProtocol
import com.bigdata.rdf.spo.ISPO

/**
 * Makes decision based on union of patterns that it has
 */
abstract class PatternCache extends Consumer with QueryController[PatternResult]
{

  val name:String

  val patterns: Set[Pat]


//  case class TuplePatterns(patterns:Set[Pat])
//
//  case class PatternRequest(patterns:Set[Pat])
//
//  case class PatternResult(results:Map[Pat,Set[Statement]])



  def register() = {
    this.rd(WatchProtocol.PatternRequest(name,this.patterns))
  }


  override def updateHandler(upd: UpdateInfo): Unit = {
    this.remove(upd.removed.filter(i=>patterns.exists(_.canBind(i))))
    this.insert(upd.inserted.filter(i=>patterns.exists(_.canBind(i))))

  }


  def insert(statements:Set[ISPO])
  def remove(statements:Set[ISPO])



}
