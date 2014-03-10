package org.denigma.semantic.cache

import org.denigma.semantic.sparql.Pat
import org.openrdf.model.{URI, Statement}
import org.denigma.semantic.controllers.{WithSemanticReader, QueryController}
import org.denigma.semantic.actors.WatchProtocol.{PatternRequest, PatternResult}
import org.denigma.semantic.actors.WatchProtocol
import com.bigdata.rdf.spo.ISPO
import scala.collection.mutable
import org.openrdf.model._
import scala.concurrent.Future
import scala.util.Try
import akka.actor.Status.Success
import org.denigma.semantic.commons.Logged
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Makes decision based on union of patterns that it has
 */
abstract class PatternCache extends Consumer with QueryController[PatternResult] with Logged
{

  val name:String

  var patterns: Set[Pat]

  var active:Boolean = false



  /**
   * Registeres pattern in patterns set
   * @param pat
   * @return
   */
  def pattern(pat:Pat) = {
    this.patterns = this.patterns + pat
    pat
  }


//  case class TuplePatterns(patterns:Set[Pat])
//
//  case class PatternRequest(patterns:Set[Pat])
//
//  case class PatternResult(results:Map[Pat,Set[Statement]])


  def onResult(p:PatternResult)

  /**
   * Activates cache
   */
  def activate() = {
    this.fill.foreach{ res=>
      res.foreach{this.onResult}
      this.active = true
    }
    this.fill.onFailure{
      case f: Throwable =>
        this.lg.error(s"cache request $name failed with: $f")
        this.active = false
    }
  }

  def reset() = {
    this.active = false
    this.activate()

  }

  /**
   *
   * @return
   */
  protected def fill: Future[Try[PatternResult]] = this.rd(WatchProtocol.PatternRequest(name,this.patterns))


  def groupByPattern(sts:Set[ISPO]): mutable.MultiMap[Pat, ISPO] = {

    val quads:mutable.MultiMap[Pat, ISPO] = new mutable.HashMap[Pat, mutable.Set[ISPO]] with mutable.MultiMap[Pat,ISPO]

    for {
      p <-patterns
      st <- sts
      if p.canBind(st)
    } quads.addBinding(p,st)
    quads
  }

  def groupBySubject(sts:Set[ISPO]):  mutable.MultiMap[Resource, (Pat, ISPO)] = {

    val quads= new mutable.HashMap[Resource, mutable.Set[(Pat,ISPO)]] with mutable.MultiMap[Resource,(Pat,ISPO)]

    for {
      p <-patterns
      st <- sts
      if p.canBind(st)
    } quads.addBinding(st.getSubject(),(p,st))
    quads
  }



}

