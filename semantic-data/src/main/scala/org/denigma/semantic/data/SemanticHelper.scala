package org.denigma.semantic.data

import scala.collection.immutable.List
import org.openrdf.query.parser.{ParsedGraphQuery, ParsedTupleQuery, QueryParserUtil}
import org.openrdf.query.{TupleQueryResult, QueryLanguage}
import arq.query
import org.openrdf.repository.sail.SailTupleQuery
import org.openrdf.query.algebra.{QueryRoot, QueryModelVisitor}


//import org.apache.log4j.Logger
import org.openrdf.repository.RepositoryResult
import scala.collection.JavaConversions._
import org.openrdf.model._
import scala.collection.immutable.List


abstract class SemanticHelper{

  type Store <:  SemanticStore

  def db:Store
  /*
  reads relationship from the repository
   */
  def withRel(rel:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
      //Resource subject, URI predicate, Value object
      //val st = v.createStatement(v.createURI("",""),v.createURI("",""),v.createURI("","")).getModified
      //val i: IV[_ <: BigdataValue, _] =   st.getStatementIdentifier

        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }


  def withSubject(sub:URI,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,null,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }


  def withObject(obj:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,null,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withSubRel(sub:URI,rel:URI,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,rel,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withRelObj(rel:URI,obj:URI,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  implicit class MagicUri(uri:URI) {

    def ~>(inferred:Boolean=true) = withSubject(uri,inferred)
    def <~(inferred:Boolean=true) = withObject(uri,inferred)
    def <~(rel:URI) = withRelObj(rel,uri)
    def ~>(rel:URI) = withSubRel(uri,rel)

  }

}

