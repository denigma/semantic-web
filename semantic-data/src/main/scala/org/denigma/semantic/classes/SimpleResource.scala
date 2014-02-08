package org.denigma.semantic.classes

import org.openrdf.model.{Literal, URI, Resource}
import org.joda.time.{DateTime, LocalDate}
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
import play.api.Play


abstract class SimpleResource(url:Resource) extends SemanticModel(url) {
  self=>

  var allResources= Map.empty[URI,Resource]


  var strings = Map.empty[URI,String]
  var longs= Map.empty[URI,Long]
  var booleans = Map.empty[URI,Boolean]
  var doubles = Map.empty[URI,Double]
  var dates = Map.empty[URI,LocalDate]
  var datetimes: Map[URI, DateTime] = Map.empty[URI,DateTime]

  var otherliterals:Map[URI,Literal]=Map.empty



  /*
  parses literal and stores then in appropriate places
   */
  def parseLiteral(p:URI,lit:Literal): Unit = lit match {
    case StringLiteral(l)=> this.strings += (p->l)
    case DoubleLiteral(l)=>this.doubles += (p->l)
    case BooleanLiteral(l)=>this.booleans += (p->l)
    case DateLiteral(l)=>this.dates += (p->l)
    case DateTimeLiteral(l)=>
      play.Logger.info(s"DATETIME's value = ${lit.stringValue}")
      this.datetimes += (p->l)
    case LongLiteral(l) => this.longs += (p->l)
    case l=>
      play.Logger.info(s"OTER LITERAL's of type ${l.getDatatype} value = ${l.stringValue}")
      this.otherliterals += (p->l)

  }



}

trait SimpleParser[SELF<:SimpleResource]
{


  def apply(model:SELF,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = (st.getPredicate,st.getObject) match  {
    case (p,o:Literal)=>

      model.parseLiteral(p,o); true
    case (p, o:Resource)=>

      model.allResources += (p->o); true


  }
}