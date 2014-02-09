package org.denigma.semantic.classes

import org.openrdf.model.{Value, URI, Resource}
import org.openrdf.model.vocabulary.{RDF, RDFS}
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

class ParentOf(url:Resource) extends ObjectOf(RDFS.SUBCLASSOF,url)

class ParentClassOf(url:Resource) extends ObjectOf(RDFS.SUBCLASSOF,url)
class ParentPropertyOf(url:Resource) extends ObjectOf(RDFS.SUBPROPERTYOF,url)

/*
Extracts TYPE of RDF Resource
 */
class OfSemanticType(values:Resource*) extends SubjectOf(RDF.TYPE,values:_*)


/*
Extracts TYPE of RDF Resource
*/
class SubjectOfMany(properties:Set[URI],objects:Set[Resource]) extends SemanticExtractor[Resource]{
  override def unapply(con: BigdataSailRepositoryConnection, s:Resource): Option[Resource] =s match {
    case v:Resource if objects.exists(value => properties.exists(p=>con.hasStatement(s, p, value, true))) =>Some(v)
    case _=>None
  }
}

/*
Extracts TYPE of RDF Resource
*/
class SubjectOf(rel:URI,values:Resource*) extends SemanticExtractor[Resource]{
  override def unapply(con: BigdataSailRepositoryConnection, s:Resource): Option[Resource] =s match {
    case v:Resource if values.exists(value =>  con.hasStatement(s, RDF.TYPE, value, true)) =>Some(v)
    case _=>None
  }
}


/*
Extracts TYPE of RDF Resource
*/
class ObjectOf(rel:URI,values:Resource*) extends SemanticExtractor[Resource]{
  override def unapply(con: BigdataSailRepositoryConnection, o:Resource): Option[Resource] =o match {
    case v:Resource if values.exists(value => con.hasStatement(value, rel,o, true)) =>Some(v)
    case _=>None
  }
}
/*
Extracts TYPE of RDF Resource
*/
class ObjectOfMany(properties:Set[URI],objects:Set[Resource]) extends SemanticExtractor[Resource]{
  override def unapply(con: BigdataSailRepositoryConnection, o:Resource): Option[Resource] =o match {
    case v:Resource if objects.exists(value => properties.exists(p=>con.hasStatement(value, p, o, true))) =>Some(v)
    case _=>None
  }
}


trait SemanticExtractor[T<:Value]{
  def unapply(con:BigdataSailRepositoryConnection, o:T):Option[T]
}