package org.denigma.semantic.reading.queries

import org.denigma.semantic.reading.modifiers.{Paginator, Binder, Slicer}
import org.openrdf.query.TupleQueryResult
import org.denigma.semantic.reading._

/*
returns results in a format that is close to original return
 */
trait SimpleQueryManager  extends SimpleSelect
  with Paginator[TupleQueryResult]  with SimpleAsk with SimpleConstruct with Slicer with Binder[TupleQueryResult]
{

  override protected def bindedHandler(str: String, binds: Map[String, String],offset: Long, limit: Long):SelectHandler[TupleQueryResult] =
  (query:String,con:ReadConnection,q:SelectQuery)=>  this.bind(con,q,binds).evaluate()

  override protected def bindedHandler(str: String, binds: Map[String, String]):SelectHandler[TupleQueryResult] =
  (query:String,con:ReadConnection,q:SelectQuery)=> this.bind(con,q,binds).evaluate()

  override protected def paginatedSelect(str: String, offset: Long, limit: Long,rewrite:Boolean):SelectHandler[TupleQueryResult] =
    (query:String,con:ReadConnection,q:SelectQuery)=>  this.slice[SelectQuery](q,offset,limit,rewrite).evaluate()

}
