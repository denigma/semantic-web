package org.denigma.semantic.quering

import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailGraphQuery, BigdataSailTupleQuery}
import com.bigdata.rdf.sparql.ast._
import org.openrdf.query.GraphQueryResult
import org.openrdf.model.Value
import play.api.Play


case class SliceQuery( offset:Long,  limit:Long) extends SliceModifier

/*
limits number of results from the query
 */
trait SliceModifier extends QueryModifier
{
  val offset:Long
  val limit:Long
  /*
On tuple Query
 */
//  override def onTupleQuery(query:String, q:BigdataSailTupleQuery)(implicit con:BigdataSailRepositoryConnection): QueryResultLike = {
//    val res: TupleQueryResult = transformTupleQuery(q).evaluate()
//    QueryResult.parse(query  ,res)
//  }

  /*
  WITH SIDE EFFECTS
   */
  override def transformTupleQuery(q:BigdataSailTupleQuery):BigdataSailTupleQuery = {
    val cont = q.getASTContainer
    val ast = cont.getOriginalAST
    if(!ast.hasSlice)  ast.setSlice(new SliceNode(offset,limit))
    cont.setOriginalAST(ast)
    //cont.setOptimizedAST(ast)
    play.Logger.info(s"SLIDE MODIFIER WITH offset= $offset and limit = $limit")
    q
  }


  /*
  TODO: remove dupliccated code
   */
  override def transformGraphQuery(g:BigdataSailGraphQuery):BigdataSailGraphQuery = {
    val cont = g.getASTContainer
    val ast = cont.getOriginalAST
    //if(!ast.hasSlice)
    ast.setSlice(new SliceNode(offset,limit))
    cont.setOriginalAST(ast)
    //cont.setOptimizedAST(ast)
    g
  }


}


case class BindingModifier(vars:(String,Value)*) extends QueryModifier {
//  val offset:Long = 0
//  val limit:Long = Config.limit
  override def transformTupleQuery(q:BigdataSailTupleQuery):BigdataSailTupleQuery = {
    vars.foreach{case (key,value)=>
      q.setBinding(key,value)
    }
    q
  }

}

