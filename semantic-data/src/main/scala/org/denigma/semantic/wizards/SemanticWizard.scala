package org.denigma.semantic.wizards

import org.openrdf.query.parser.QueryParserUtil
import org.openrdf.query.algebra.QueryModelVisitor
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.util.{Failure, Try}
import org.denigma.semantic.SG
import com.hp.hpl.jena.query.{Syntax, QueryFactory, Query}
import org.denigma.semantic.data.{QueryResultLike, SemanticStore}


//import org.apache.log4j.Logger
import org.openrdf.repository.RepositoryResult
import scala.collection.JavaConversions._
import org.openrdf.model._
import scala.collection.immutable.List


abstract class SemanticWizard{

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


  def withSubject(sub:Resource,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,null,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }


  def withObject(obj:Value,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,null,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withSubRel(sub:Resource,rel:URI,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,rel,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withRelObj(rel:URI,obj:Value,inferred:Boolean=true): List[Statement] = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  implicit class MagicUri(uri:Resource) {

    def ~>(inferred:Boolean=true) = withSubject(uri,inferred)
    def <~(inferred:Boolean=true) = withObject(uri,inferred)
    def <~(rel:URI) = withRelObj(rel,uri)
    def ~>(rel:URI) = withSubRel(uri,rel)
    def of(tp:URI)(implicit con: BigdataSailRepositoryConnection) = isOfType(uri,tp)(con)

  }

  def isOfType(s:Resource,tp:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = {
    val str =       """
                      | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                      | PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                      |
                      | ASK WHERE {?subject a / rdfs:subClassOf * ?object }
                    """.stripMargin
    SG.db.quickAsk(str)(con).recover{
      case r=>
        db.lg.error(s"isOfType for resource ${s.stringValue()} and type ${tp.stringValue()} FAILED with ${r.toString}"); false
    }.get
  }



}

