package org.denigma.semantic.classes
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.data.SemanticStore
import org.denigma.semantic.SG
import org.openrdf.model._

import org.denigma.semantic.data.QueryResult
import org.denigma.semantic.SG
import org.openrdf.model
import com.bigdata.rdf.vocab.decls

import org.openrdf.model.vocabulary
import scala.util.Try
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SemanticResource, SemanticModel}
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._
import javax.xml.datatype.XMLGregorianCalendar
import org.joda.time._

trait ResourceLike{
  val isClass:Boolean
  val isBlankNode:Boolean
  val isContainer:Boolean
}



trait ResourceParser[SELF<:SemanticResource]
{


  def apply(model:SELF,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = (st.getPredicate,st.getObject) match  {


    case _ =>
      val l:Literal=null
      false


  }
}


/**
* Created by antonkulaga on 2/6/14.
*/
class SemanticResource(url:Resource)  extends SemanticModel(url) {
  self=>

  val isClass = false
  var isBlankNode = false


  var resources:Map[URI,SemanticResource]= Map.empty

  var types:Map[URI,SemanticClass] = Map.empty

  //var incoming:Map[URI,Resource] = Map.empty


  object SemanticResourceParser extends ResourceParser[self.type] with self.ModelParser

  self.parsers = SemanticResourceParser::self.parsers
}


object SemanticClass extends OfSemanticType(RDFS.CLASS,OWL.CLASS)
class SemanticClass(url:Resource) extends SemanticResource(url){

  self=>

  override val isClass = true

  var subClasses:Map[URI,SemanticClass] = Map.empty
  var parentClasses:Map[URI,SemanticClass] = Map.empty




}
