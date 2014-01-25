package org.denigma.semantic.data


import com.bigdata.rdf.sail._
import scala.util.Try
import org.openrdf.query.{GraphQueryResult, QueryLanguage}
import com.hp.hpl.jena.query._
import org.openrdf.query.algebra._
import scala.util.Failure
import org.openrdf.query.parser.ParsedUpdate
import scala.collection.JavaConversions._
import com.bigdata.rdf.sparql.ast.{IQueryNode, SliceNode, ASTContainer}


/**
 * Created by antonkulaga on 1/23/14.
 */
abstract class SemanticQueries   extends RDFStore{

  /*
  it should be safe in future but now it is only limited
   */
  def safeQuery(str:String, limit:Long,offset:Long):Try[QueryResult] = this.query(this.alterQuery(str,limit,offset))

  /*
  adds limit and offset to the query
   */
  def alterQuery(str:String,limit:Long,offset:Long) = if(limit<1 && offset <1) str else {
    val q: Query =   QueryFactory.create(str, Syntax.syntaxSPARQL_11)
    if(limit>0 && !q.hasLimit)q.setLimit(limit)
    if(offset>0)q.setOffset(offset)
    q.toString(Syntax.syntaxSPARQL_11)
  }

  /*
 runs query over db
  */
  def query(str:String):Try[QueryResult] = read{
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

  def update(query:String) = write {
    implicit wr=>
      val upd = wr.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,query,"http://denigma.org/resource/")
      val p: ParsedUpdate = upd.getParsedUpdate
      val res = p.getUpdateExprs.toList
      val fst: UpdateExpr = res.head

      upd.execute()
      ???
  }


  def tupleQuery(query:String, q:BigdataSailTupleQuery) = {
//    val c: ASTContainer = q.getASTContainer
//    val context: BigdataASTContext = new BigdataASTContext(q.getTripleStore)
//
//    val ast = c.getOriginalAST
//    val sl = new SliceNode(0,10)
//    ast.setSlice(sl)

    QueryResult.parse(query  ,q.evaluate())
  }

}
