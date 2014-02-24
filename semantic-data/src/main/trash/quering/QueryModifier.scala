package org.denigma.semantic.quering

import com.bigdata.rdf.sail._
import org.openrdf.query.{GraphQueryResult, TupleQueryResult}
import com.bigdata.rdf.sparql.ast.{VarNode, SliceNode, OrderByNode}
import org.denigma.semantic.reading.{QueryResultLike, AskResult}
import org.denigma.semantic.reading.selections.SelectResult


object DefaultQueryModifier extends QueryModifier

/*
trait that defines how string queries behave
 */
trait QueryModifier {
  /*
  handler that deals with BigDataSailQuery
   */
  def onQuery:PartialFunction[(String,BigdataSailRepositoryConnection,BigdataSailQuery),QueryResultLike] =  {
    case (str,con,q: BigdataSailTupleQuery)=>
      onTupleQuery(str,q)(con)

    case (str,con,g: BigdataSailGraphQuery) =>  onGraphQuery(str,g)(con)
    case (str,con,b: BigdataSailBooleanQuery)=> onAskQuery(str,b)(con)
    case (str,con,unknown) => throw new RuntimeException(s"Unknown query type of $str")
  }


  /*
  On graph Query
   */

  def onGraphQuery(query:String, g:BigdataSailGraphQuery)(implicit con:BigdataSailRepositoryConnection): QueryResultLike = {
    val res: GraphQueryResult = transformGraphQuery(g).evaluate()
    SelectResult.parse(query  ,res)
  }


 def transformTupleQuery(q:BigdataSailTupleQuery):BigdataSailTupleQuery = q
 def transformGraphQuery(g:BigdataSailGraphQuery):BigdataSailGraphQuery= g

  /*
  On tuple Query
   */
  def onTupleQuery(query:String, q:BigdataSailTupleQuery)(implicit con:BigdataSailRepositoryConnection): QueryResultLike = {
    val res: TupleQueryResult = transformTupleQuery(q).evaluate()
    SelectResult.parse(query  ,res)
  }

  /*
  On graph Query
   */


  def onAskQuery(query:String,b: BigdataSailBooleanQuery)(implicit con:BigdataSailRepositoryConnection):QueryResultLike = AskResult(query,b.evaluate())

}