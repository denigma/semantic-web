package org.denigma.semantic.data


import com.bigdata.rdf.sail._
import scala.util.Try
import org.openrdf.query.QueryLanguage
import com.hp.hpl.jena.query._
import scala.util.Failure
import org.denigma.semantic.SG

trait QueryWizard {
  implicit class MagicQuery(q:Query) {

    def withLimit(limit:Long, always:Boolean = true)= {
      if(limit>0)
        if(always || !q.hasLimit) q.setLimit(limit)
      q
    }

    def withOffset(offset:Long, always:Boolean = true) = {
      if(offset>0)
              if(always || !q.hasOffset) q.setOffset(offset)
      q
    }

//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }
//
//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }

  }
}



/**
 * Created by antonkulaga on 1/23/14.
 */
abstract class SemanticQueries   extends RDFStore{


  /*
  it should be safe in future but now it is only limited
   */
  def safeQuery(str:String, limit:Long,offset:Long):Try[QueryResultLike] =
    this.alterQuery(str,limit,offset).map(query(_,str)).getOrElse(Failure(new RuntimeException(s"Unknown query type of $str")))

  /*
  adds limit and offset to the query
   */
  def alterQuery(str:String,limit:Long,offset:Long, sortVar:String="") = Try {
    if(limit<1 && offset <1) str else {
      val q: Query =   QueryFactory.create(str, Syntax.syntaxSPARQL_11)
      SG.MagicQuery(SG.MagicQuery(q).withLimit(limit,always = false)).withOffset(offset,always = false).toString(Syntax.syntaxSPARQL_11)
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
