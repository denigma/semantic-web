package org.denigma.semantic.classes


import org.openrdf.model._
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection


//
//class Bag(url:Resource,con:BigdataSailRepositoryConnection) extends BlankNode(url) with ResourceLike{
//  override val isContainer: Boolean = true
//
//}
//
//
//
//class BlankNode(val url:Resource)  extends SemanticModel{
//  val isBlankNode = true
//  val isContainer: Boolean = false
//  val isClass: Boolean = false
//
//}

object BlankNode extends SemanticExtractor[Value]{
  override def unapply(con: BigdataSailRepositoryConnection, o:Value): Option[Value] =o match {
    case v:BNode=>Some(v)
    case _=>None
  }
}