package models

import org.denigma.semantic
import org.denigma.semantic.classes._

import org.denigma.semantic.{WithSemanticPlatform, SemanticPlatform, WI, Prefixes}
import org.openrdf.model.impl.URIImpl
import org.openrdf.model._
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.vocabulary._

object MenuRepository {

  def findMenu(url:URI):Option[Menu] = ???

  def findMenu(nameSpace:String,name:String):Option[Menu] = findMenu(new URIImpl(nameSpace+name))
}


class Menu(url:URI) extends SimpleResource(url)  {


  var label:String = url.stringValue()

  override def init(){
  object MenuParser extends MenuParser[this.type]
  this.parsers = MenuParser::this.parsers
  }

}

class MenuParser[SELF<:Menu] extends SimpleParser[SELF] with WithSemanticPlatform{

  def onMenu:onPropertyObject = {

    case (out,RDFS.LABEL,o:Literal) =>   out.model.label = o.stringValue()

    case (out, p:URI, o:Resource) if sp.isOfType(o,Prefixes.Pages.PAGE)(out.con)=>  out.model.outgoingResources.addBinding(p,o)

  }

  override def parsePropertyObject:onPropertyObject = this.onMenu orElse onLiteralOrOther
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