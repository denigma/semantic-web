package org.denigma.semantic.classes

import org.openrdf.model.{Value, Resource, URI}
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.vocabulary.{RDFS, RDF}

/*
this class represent property class of semanticweb
 */
class SemanticProperty(url:URI) extends SemanticClass(url){
  override val isProperty = true

  var domains =  Map.empty[Resource,SemanticClass]
  var ranges =  Map.empty[Resource,SemanticClass]
  var parentProperties = Map.empty[Resource,SemanticProperty]
  var subProperties = Map.empty[Resource,SemanticProperty]


  def is(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDF.TYPE,o)(con)

  def isMyPropertyParent(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.SUBPROPERTYOF,o)(con)

  def isMySubProperty(s:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iObjectOf(s,RDFS.SUBPROPERTYOF)(con)

  def isMyDomain(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.DOMAIN,o)(con)

  def isMyRange(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.RANGE,o)(con)




  override def init(){
    object SemanticPropertyParser extends PropertyParser[this.type]
    this.parsers = SemanticPropertyParser::this.parsers
  }


}

class PropertyParser[SELF<:SemanticProperty] extends ClassParser[SELF]
{

  def parseSubProperties:onSubjectProperty =
  {
    case (inc:IncomingParams[SELF],s:URI,p:URI) if inc.model.isMySubProperty(s)(inc.con)=>
      val f = extractProperty(inc,s)
      inc.model.subProperties = inc.model.subProperties + (s->f)
  }

  def onParentProperty:onPropertyObject = {
    case (out,p, o:URI) if out.model.url!=o &&  out.model.isMyPropertyParent(o)(out.con)=>

      val f = extractProperty(out,o)
      out.model.parentProperties = out.model.parentProperties + (o->f)

  }

  def onDomainRange:onPropertyObject = {
    case (out,p, o:URI) if out.model.url!=o &&  out.model.isMyDomain(o)(out.con)=>

      val f = extractClass(out,o)
      out.model.domains = out.model.domains + (o->f)
      f.domainOf += out.model.url->out.model

    case (out,p, o:URI) if out.model.url!=o &&  out.model.isMyRange(o)(out.con)=>

      val f = extractClass(out,o)
      out.model.ranges = out.model.ranges + (o->f)
      f.rangeOf += out.model.url->out.model

  }

  override def parseSubjectProperty:onSubjectProperty =parseSubProperties.orElse(super.parseSubjectProperty)

  override def parsePropertyObject:PartialFunction[(OutgoingParams[SELF],URI,Value),Unit] = this.onParentProperty.orElse(super.parsePropertyObject)


}