package org.denigma.semantic.classes

import org.openrdf.model.{Value, URI, Resource}
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.vocabulary.{RDFS, RDF}


/*
contains useful methods to check resources properties
 */
trait SmartResource {
  //trait inherits from SimpleResource
  self:SimpleResource=>

  def iObjectOf(sub:Resource,prop:URI)(implicit con: BigdataSailRepositoryConnection): Boolean = con.hasStatement(sub,prop,this.url, true)
  def iSubjectOf(prop:URI,obj:Value)(implicit con: BigdataSailRepositoryConnection): Boolean = con.hasStatement(url,prop,obj, true)

  def isMyType(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDF.TYPE,o)(con)
  def containsMe(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.MEMBER,o)(con)



}

trait SmartClass extends SmartResource{
  self:SimpleResource=>
    /*
  checkers of relationships between this semanticresource, a property and some other resource
   */
    def isMyParentClass(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.SUBCLASSOF,o)(con)

    def isMyChildClass(s:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iObjectOf(s,RDFS.SUBCLASSOF)(con)


    def iDomainOf(u:URI)(implicit con: BigdataSailRepositoryConnection):Boolean = iObjectOf(u,RDFS.DOMAIN)(con)

    def iRangeOf(u:URI)(implicit con: BigdataSailRepositoryConnection):Boolean = iObjectOf(u,RDFS.RANGE)(con)

}

trait SmartProperty extends SmartClass {
  self:SimpleResource=>

  def is(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDF.TYPE,o)(con)

  def isMyPropertyParent(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.SUBPROPERTYOF,o)(con)

  def isMySubProperty(s:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iObjectOf(s,RDFS.SUBPROPERTYOF)(con)

  def isMyDomain(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.DOMAIN,o)(con)

  def isMyRange(o:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = iSubjectOf(RDFS.RANGE,o)(con)
}