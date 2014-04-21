package org.denigma.semantic.classes

import org.openrdf.model.{Value, URI, Resource}


/*
SCALA model of SemanticWeb class that represent a class in semantic web
 */
class SemanticClass(url:Resource) extends SemanticResource(url) with SmartClass{  self=>

  override val isClass = true

  //var subClasses=  Map.empty[Resource,SemanticClass]
  var parentClasses =  Map.empty[Resource,SemanticClass]
  var subClasses = Map.empty[Resource,SemanticClass]
  var domainOf = Map.empty[Resource,SemanticProperty]
  var rangeOf = Map.empty[Resource,SemanticProperty]

  /*
  overridable method to change part of init logic (mostly - parsers assignments)
   */
  override def init(){
    object SemanticClassParser extends ClassParser[self.type]
    self.parsers = SemanticClassParser::self.parsers
  }

}

/*]
Parser that fills SemanticClass models with properties, subclasses, children classes
 */
trait ClassParser[SELF<:SemanticClass] extends ResourceParser[SELF]
{
  def extractProperty(params:TraverseParams[SELF],s:URI):SemanticProperty =
    params.path.collectFirst{  case (k,sp:SemanticProperty) if sp.url==s =>sp}
      .getOrElse{
      val sp = new SemanticProperty(s)
      sp.loadAll(params)
      //sc.subClasses = sc.subClasses + (model.url->model)
      sp
    }

  def parseChildren:onSubjectProperty =
    {
    case (inc:IncomingParams[SELF],s:Resource,p:URI) if inc.model.isMyChildClass(s)(inc.con)=>
      val f: SemanticClass = extractClass(inc,s)
      inc.model.subClasses = inc.model.subClasses + (s->f)
    }

  /*
  finds properties with this class as range or domain
   */
  def parseProps:onSubjectProperty =
  {
    case (inc:IncomingParams[SELF],s:URI,p:URI) if inc.model.iDomainOf(s)(inc.con)=>
      val f = extractProperty(inc,s)
      inc.model.domainOf = inc.model.domainOf + (s->f)
      f.domains = f.domains + (inc.model.url->inc.model)

    case (inc:IncomingParams[SELF],s:URI,p:URI) if inc.model.iRangeOf(s)(inc.con)=>
      val f = extractProperty(inc,s)
      inc.model.rangeOf = inc.model.rangeOf + (s->f)
      f.ranges = f.ranges + (inc.model.url->inc.model)
  }

  override def parseSubjectProperty:onSubjectProperty =parseProps.orElse(parseChildren).orElse(parseIncoming)


  def onParent:onPropertyObject = {
    case (out,p, o:Resource) if out.model.url!=o &&  out.model.isMyParentClass(o)(out.con)=>

      val f: SemanticClass = extractClass(out,o)
      out.model.parentClasses = out.model.parentClasses + (o->f)
  }

  override def parsePropertyObject:PartialFunction[(OutgoingParams[SELF],URI,Value),Unit] = this.onParent.orElse(super.parsePropertyObject)

}







