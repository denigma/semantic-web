package org.denigma.semantic.controllers.readers

import akka.actor.ActorRef
import akka.util.Timeout
import com.bigdata.rdf.sail.{BigdataSailTupleQuery, BigdataSailGraphQuery, BigdataSailBooleanQuery, BigdataSailRepositoryConnection}
import org.denigma.semantic.reading.QueryResultLike

abstract class Modifier[T](val reader:ActorRef,val readTimeout:Timeout) extends AnyQueryController[T]



/*
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

 */