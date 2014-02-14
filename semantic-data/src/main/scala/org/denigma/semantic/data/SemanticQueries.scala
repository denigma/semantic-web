package org.denigma.semantic.data


import com.bigdata.rdf.sail._
import scala.util.Try
import org.openrdf.query.QueryLanguage
import com.hp.hpl.jena.query._
import scala.util.Failure
import org.denigma.semantic.SG
import org.openrdf.model.Resource





/**
 * Created by antonkulaga on 1/23/14.
 */
abstract class SemanticQueries   extends RDFStore{


  def ask(str:String):Try[Boolean] = read{con:BigdataSailRepositoryConnection=>
    this.quickAsk(str)(con)
  }.flatten

  def quickAsk(str:String)(con:BigdataSailRepositoryConnection):Try[Boolean] =
  {
    Try{
    val q = con.prepareBooleanQuery(QueryLanguage.SPARQL,str,"http://denigma.org/resource/")
    q.evaluate()
    }
  }


  /*
 runs query over db
  */
  def query(str:String, initialQuery:String=""):Try[QueryResultLike] = read{
    implicit r=>

      r.prepareNativeSPARQLQuery(QueryLanguage.SPARQL,str,"http://denigma.org/resource/") match //TODO: delete hardcoded URI from here
      {
        case q: BigdataSailTupleQuery=>
          tupleQuery(str,q)
        case g: BigdataSailGraphQuery=>

          return Failure(new NotImplementedError(s"Graph query is not implemented yet $str"))

        case b: BigdataSailBooleanQuery=>AskResult(str,b.evaluate())

        case _ => return Failure(new RuntimeException(s"Unknown query type of $str"))
      }
  }

  def tupleQuery(query:String, q:BigdataSailTupleQuery): QueryResult = {
    QueryResult.parse(query  ,q.evaluate())
  }



//  def update(query:String, nameSpace:String = SG.db.WI) = write {
//    implicit wr=>
//
//      val upd = wr.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,query,nameSpace)
//      val p: ParsedUpdate = upd.getParsedUpdate
//      val res = p.getUpdateExprs.toList
//      val fst: UpdateExpr = res.head
//
//      upd.execute()
//      ???
//  }
//
//
//
//
//  def graphQuery(query:String, q:BigdataSailGraphQuery) = {
//    val res = q.evaluate()
//    ???
//  }
}
