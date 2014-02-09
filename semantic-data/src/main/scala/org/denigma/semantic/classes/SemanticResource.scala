package org.denigma.semantic.classes

import org.openrdf.model._

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._
import scala.util.Try


trait ResourceLike{
  val isClass:Boolean
  val isBlankNode:Boolean
  val isContainer:Boolean
  val isProperty:Boolean
}


/**
* class to handler resources with all props and so on
*/
class SemanticResource(url:Resource)  extends SimpleResource(url) with ResourceLike{

  self=>

  def iObjectOf(sub:Resource,prop:URI)(implicit con: BigdataSailRepositoryConnection): Boolean = con.hasStatement(sub,prop,this.url, true)
  def iSubjectOf(prop:URI,obj:Value)(implicit con: BigdataSailRepositoryConnection): Boolean = con.hasStatement(url,prop,obj, true)

  def isMyType(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDF.TYPE,o)(con)
  def containsMe(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.MEMBER,o)(con)

  var types =  Map.empty[Resource,SemanticClass]

  val isClass = false
  val isBlankNode = false
  val isContainer = false
  val isProperty = false



  var resources:Map[URI,SemanticResource]= Map.empty


  /*
  loads all properties of the resource
   */
  override def loadAll(params:LoadParamsLike):Try[Unit] = {
    loadOutgoing(params).map(f=>loadInComing(params))
  }

  def loadInComing(params:LoadParamsLike) =
    this.loadWith(params.con.getStatements(null,null,url,true),params)((st,np)=>IncomingParams[this.type](this,st,params.path,params.maxDepth)(params.con))




  //var incoming:Map[URI,Resource] = Map.empty



  override def init(){

    object SemanticResourceParser extends SimpleParser[self.type]
    self.parsers = SemanticResourceParser::self.parsers
  }



}




class ResourceParser[SELF<:SemanticResource] extends SimpleParser[SELF]
{
  def onType:onPropertyObject = {
    case (out,p, o:Resource) if out.model.isMyType(o)(out.con)=>
      val f: SemanticClass = out.path.collectFirst{  case (k,sl:SemanticClass) if sl.url==o =>sl}
        .getOrElse{
        val sc = new SemanticClass(o)
        sc.loadAll(out)
        sc
      }
      out.model.types = out.model.types + (o->f)
  }

  override def parsePropertyObject:PartialFunction[(OutgoingParams[SELF],URI,Value),Unit] = this.onType.orElse(super.parsePropertyObject)

}



