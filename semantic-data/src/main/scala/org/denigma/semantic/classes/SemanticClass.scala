package org.denigma.semantic.classes

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{Value, URI, Resource, Statement}
import org.openrdf.model.vocabulary._


/*
SCALA model of SemanticWeb class that represent a class in semantic web
 */
class SemanticClass(url:Resource) extends SemanticResource(url){  self=>

  override val isClass = true



  /*
  checkers of relationships between this semanticresource, a property and some other resource
   */
  def isMyParentClass(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.SUBCLASSOF,o)(con)

  def isMyPropertyParent(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.SUBPROPERTYOF,o)(con)

  def iDomainOf(u:URI)(implicit con: BigdataSailRepositoryConnection):Boolean = iObjectOf(u,RDFS.DOMAIN)(con)

  def iRangeOf(u:URI)(implicit con: BigdataSailRepositoryConnection):Boolean = iObjectOf(u,RDFS.RANGE)(con)




  //var subClasses=  Map.empty[Resource,SemanticClass]
  var parentClasses =  Map.empty[Resource,SemanticClass]
  var domainOf = Map.empty[Resource,SemanticClass]

  /*
  overridable method to change part of init logic (mostly - parsers assignments)
   */
  override def init(){
    object SemanticClassParser extends ClassParser[self.type]
    self.parsers = SemanticClassParser::self.parsers
  }

}

/*]
Parser that fills SemanticClass models
 */
trait ClassParser[SELF<:SemanticClass] extends ResourceParser[SELF]
{


  def onParent:onPropertyObject = {
    case (out,p, o:Resource) if out.model.url!=o &&  out.model.isMyParentClass(o)(out.con)=>

    val f: SemanticClass = out.path.collectFirst{  case (k,sl:SemanticClass) if sl.url==o =>sl}
      .getOrElse{
      val sc = new SemanticClass(o)
      sc.loadAll(out)
      //sc.subClasses = sc.subClasses + (model.url->model)
      sc
    }
    out.model.parentClasses = out.model.parentClasses + (o->f)
  }

  override def parsePropertyObject:PartialFunction[(OutgoingParams[SELF],URI,Value),Unit] = this.onParent.orElse(super.parsePropertyObject)


}







