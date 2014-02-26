package org.denigma.semantic.reading.selections

import scala.util.Try
import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.commons.WI
import org.openrdf.query.TupleQueryResult

/*
can make selects
 */
trait ISelect[T] extends SelectReader{

  def select(query:String): Try[T] = this.selectQuery[T](query,selectHandler)(WI.RESOURCE)
  protected def selectHandler:SelectQuerying[T]
}

trait SimpleSelect extends ISelect[TupleQueryResult]{
  override protected def selectHandler:SelectQuerying[TupleQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonSelect extends ISelect[QueryResultLike]{
  override protected def selectHandler:SelectQuerying[QueryResultLike]  = (str,con,q)=>SelectResult.parse(str,q.evaluate())
}