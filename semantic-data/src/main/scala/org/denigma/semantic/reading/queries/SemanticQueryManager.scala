package org.denigma.semantic.reading.queries

import org.denigma.semantic.reading.{QueryResultLike, ReadConnection}
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.questions.JsonAsk
import org.denigma.semantic.reading.constructs.JsonConstruct
import org.openrdf.model.Value
import org.denigma.semantic.reading.modifiers.Binder

/*
Class that can do a lot of operation
 */
trait SemanticQueryManager extends PaginatedQueryManager[QueryResultLike]
    with Binder[QueryResultLike] with JsonSelect with JsonAsk with JsonConstruct
{
  override protected def bindedHandler(str: String,binds:Map[String,String], offset: Long, limit: Long):SelectQuerying[QueryResultLike] =
    (query:String,con:ReadConnection,q:SelectQuery)=>
  {
      val mq:SelectQuery = this.bind(con,slice[SelectQuery](q,offset,limit),binds)
      SelectResult.parse(query,mq.evaluate())
  }

  override protected def bindedHandler(str: String, params: Map[String,String]):SelectQuerying[QueryResultLike] =
    (query:String,con:ReadConnection,q:SelectQuery)=>
  {
      val mq:SelectQuery = this.bind(con,q,params)
      SelectResult.parse(query,mq.evaluate())
  }

  override protected def paginatedSelect(str: String, offset: Long, limit: Long):SelectQuerying[QueryResultLike] =
    (query:String,con:ReadConnection,q:SelectQuery)=>
  {
    SelectResult.parse(query,this.slice[SelectQuery](q,offset,limit).evaluate())
  }
}