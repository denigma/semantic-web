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


  var types =  Map.empty[Resource,SemanticClass]

  val isClass = false
  val isBlankNode = false
  val isContainer = false
  val isProperty = false

  val incomingResources = new SemanticProperties[Resource]()


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



/*
Parses SemanticResource and extracts valuable information about types, classes and so on
 */
class ResourceParser[SELF<:SemanticResource] extends SimpleParser[SELF]
{
  def extractClass(params:TraverseParams[SELF],s:Resource):SemanticClass =
    params.path.collectFirst{  case (k,sl:SemanticClass) if sl.url==s =>sl}
      .getOrElse{
      val sc = new SemanticClass(s)
      sc.loadAll(params)
      //sc.subClasses = sc.subClasses + (model.url->model)
      sc
    }

    def parseIncoming:onSubjectProperty = {
      case (out, p:URI, o:Resource)=>  out.model.outgoingResources.addBinding(p,o)

    }

      def parseSubjectProperty:onSubjectProperty = parseIncoming


   override def parse:PartialFunction[TraverseParams[SELF],Unit] = {

    case in:IncomingParams[SELF]=>parseSubjectProperty((in,in.st.getSubject,in.st.getPredicate))

    case out:OutgoingParams[SELF]=>parsePropertyObject((out,out.st.getPredicate,out.st.getObject))

    case p=>play.Logger.info(s"unknown parse params: ${p.toString}")
  }

  def onType:onPropertyObject = {
    case (out,p, o:Resource) if out.model.isMyType(o)(out.con)=>
      val f: SemanticClass = extractClass(out,o)
      out.model.types = out.model.types + (o->f)
  }

  override def parsePropertyObject:PartialFunction[(OutgoingParams[SELF],URI,Value),Unit] = this.onType.orElse(super.parsePropertyObject)

}



