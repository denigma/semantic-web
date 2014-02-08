package org.denigma.semantic.classes

import org.openrdf.model.Resource
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SemanticResource, SemanticModel}
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._


import org.openrdf.model.impl.URIImpl
import org.openrdf.model.{Statement, URI, Resource}


class Bag(url:Resource,con:BigdataSailRepositoryConnection) extends BlankNode(url) with ResourceLike{
  override val isContainer: Boolean = true

}



class BlankNode(url:Resource)  extends SemanticModel(url){
  val isBlankNode = true
  val isContainer: Boolean = false
  val isClass: Boolean = false

}

object BlankNode extends SemanticExtractor[Value]{
  override def unapply(con: BigdataSailRepositoryConnection, o:Value): Option[Value] =o match {
    case v:BNode=>Some(v)
    case _=>None
  }
}