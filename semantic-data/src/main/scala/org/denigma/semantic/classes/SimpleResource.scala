package org.denigma.semantic.classes


import org.joda.time.{DateTime, LocalDate}

import org.openrdf.model._
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import scala.util.Try


case class OutgoingParams[T<:SemanticModel](model:T,st:Statement,path:Map[Resource,SemanticModel],maxDepth:Int)(implicit val con:BigdataSailRepositoryConnection) extends TraverseParams[T]
case class IncomingParams[T<:SemanticModel](model:T,st:Statement,path:Map[Resource,SemanticModel],maxDepth:Int)(implicit val con:BigdataSailRepositoryConnection) extends TraverseParams[T]


class SimpleResource(val url:Resource) extends SemanticModel {
  self=>



  val outgoingResources = new SemanticProperties[Resource]()


  val strings = new SemanticProperties[String]()
  val longs = new SemanticProperties[Long]()
  val booleans  = new SemanticProperties[Boolean]()
  val doubles  = new SemanticProperties[Double]()
  val dates  = new SemanticProperties[LocalDate]()
  val dateTimes  = new SemanticProperties[DateTime]()
  val otherliterals = new SemanticProperties[Literal]()

  //val allLiterals = new SemanticProperties[Literal]()


  /*
    loads all properties of the resource
     */
  def loadAll(params:LoadParamsLike):Try[Unit] = {
    loadOutgoing(params)
   }


  def loadOutgoing(params:LoadParamsLike) = {
    this.loadWith(params.con.getStatements(url,null,null,true),params)((st,p)=>OutgoingParams[this.type](this,st,p,params.maxDepth)(params.con))
  }


  /*
  parses literal and stores then in appropriate places
   */
  def parseLiteral(p:URI,lit:Literal): Unit = lit match {
    case StringLiteral(l:String)=> this.strings.addBinding(p,l)
    case DoubleLiteral(l:Double)=>this.doubles.addBinding(p,l)
    case BooleanLiteral(l:Boolean)=>this.booleans.addBinding(p,l)
    case DateLiteral(l:LocalDate)=>this.dates.addBinding(p,l)
    case DateTimeLiteral(l:DateTime)=> this.dateTimes.addBinding(p,l)
    case LongLiteral(l) => this.longs.addBinding(p,l)
    case l=> this.otherliterals.addBinding(p,l)

  }

  def init(){

    object SimpleParser extends SimpleParser[self.type]
    self.parsers = SimpleParser::self.parsers
  }
  init()



}

/*
Extracts info about semanticweb resource.
This one is the simpliest, it only gets literals and adds resources to one collection
 */
class SimpleParser[SELF<:SimpleResource] extends ModelParser[SELF]
{
  /*
  partial function for outgoing traversals
   */
  type onPropertyObject = PartialFunction[(OutgoingParams[SELF],URI,Value),Unit]
  /*
  partial functions for incoming traversals
   */
  type onSubjectProperty = PartialFunction[(IncomingParams[SELF],Resource,URI),Unit]


  def onLiteralOrOther:onPropertyObject = {
    case (out,p:URI,o:Literal)=>     out.model.parseLiteral(p,o)

    case (out, p:URI, o:Resource)=>  out.model.outgoingResources.addBinding(p,o)
  }

  def parsePropertyObject:onPropertyObject = onLiteralOrOther

  override def parse:PartialFunction[TraverseParams[SELF],Unit] = {

    case out:OutgoingParams[SELF]=>parsePropertyObject((out,out.st.getPredicate,out.st.getObject))

    case p=>play.Logger.info(s"unknown parse params: ${p.toString}")
  }
}
/*
Just a multimap to store props
 */
class SemanticProperties[T] extends  collection.mutable.HashMap[URI, collection.mutable.Set[T]] with collection.mutable.MultiMap[URI, T]