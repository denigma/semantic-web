package org.denigma.semantic.data

import com.bigdata.rdf.sail._
import scala.util.{Failure, Success, Try}
import org.openrdf.query.{GraphQueryResult, TupleQueryResult, TupleQuery, QueryLanguage}
import com.hp.hpl.jena.sparql.syntax._
import com.hp.hpl.jena.sparql._
import com.hp.hpl.jena.query._
import scala.util.Success
import scala.util.Failure
import org.openrdf.query.algebra._
import com.hp.hpl.jena.rdf.model.{Resource, ModelFactory}
import scala.util.Success
import scala.util.Failure
import org.openrdf.query.parser.{ParsedTupleQuery, ParsedUpdate}
import scala.collection.JavaConversions._

/**
 * Created by antonkulaga on 1/20/14.
 */
abstract class SemanticStore(implicit lg:org.slf4j.Logger) {

  val repo: BigdataSailRepository

  def read[T](action:BigdataSailRepositoryConnection=>T):Try[T]= {
    val con = repo.getReadOnlyConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      lg.error("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }

  /*
  writes something and then closes the connection
   */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = repo.getConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }

  /*
 Shutdown repository
  */
  def close()={
    repo.shutDown()
  }

  /*
does something with Sesame connection and then closes it
 */
  def withConnection(con:BigdataSailRepositoryConnection)(action:BigdataSailRepositoryConnection=>Unit) = {
    if(!con.isReadOnly) con.setAutoCommit(false)
    Try {
      action(con)
      if(!con.isReadOnly) con.commit()
    }.recover{case f=>lg.error(f.toString)}
    lg.debug("operation successful")
    con.close()
  }


  /*
  writes something and then closes the connection
   */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean = this.readWrite[Unit](action) match
  {
    case Success(_)=>true
    case Failure(e)=>false
  }

  def tupleQuery(query:String, q:BigdataSailTupleQuery) = QueryResult.parse(query,q.evaluate())


  /*
 runs query over db
  */
  def query(str:String, lan: QueryLanguage= QueryLanguage.SPARQL):Try[QueryResult] = read{
    implicit r=>
      r.prepareNativeSPARQLQuery(lan,str,"http://denigma.org/resource/") match //TODO: delete hardcoded URI from here
      {
        case q: BigdataSailTupleQuery=>
          tupleQuery(str,q)
        case g: BigdataSailGraphQuery=>

          val res: GraphQueryResult = g.evaluate()
          return Failure(new NotImplementedError(s"Graph query is not implemented yet $str"))

        case b: BigdataSailBooleanQuery=>AskResult(str,b.evaluate())

        case _ => return Failure(new RuntimeException(s"Unknown query type of $str"))
      }
  }

  def update(query:String,lan:QueryLanguage = QueryLanguage.SPARQL) = write {
    implicit wr=>
      val upd = wr.prepareNativeSPARQLUpdate(lan,query,"http://denigma.org/resource/")
      val p: ParsedUpdate = upd.getParsedUpdate
      val res = p.getUpdateExprs.toList
      val fst: UpdateExpr = res.head

        upd.execute()
      ???
  }

  def safeQuery(query:String) = {
    val q: Query =   QueryFactory.create("SELECT ?subject ?pred WHERE { ?subject ?pred <http://denigma.org/resource/Aging>.}", Syntax.syntaxSPARQL_11)
    q.addGroupBy("subject")

    val m = ModelFactory.createDefaultModel();
    //val pattern =  Triple.create(Var.alloc("s"), Var.alloc("p"), Var.alloc("o"));
    val res: Resource = m.createResource("http://denigma.org/resource/")
    q.addNamedGraphURI(res.getURI)
    val str = q.toString(Syntax.syntaxSPARQL_11)
    str
  }



}
class Visitor extends ElementVisitorBase{

}
