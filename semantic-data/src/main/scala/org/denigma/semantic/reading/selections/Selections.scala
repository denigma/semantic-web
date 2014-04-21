package org.denigma.semantic.reading.selections

import scala.util.Try
import org.denigma.semantic.reading.QueryResultLike
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf.vocabulary.WI


/*
can make selects
 */
trait ISelect[T] extends SelectReader{

  def select(query:String): Try[T] = this.selectQuery[T](query,selectHandler)(WI.RESOURCE)
  protected def selectHandler:SelectHandler[T]
}

trait SimpleSelect extends ISelect[TupleQueryResult]{
  override protected def selectHandler:SelectHandler[TupleQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonSelect extends ISelect[QueryResultLike]{
  override protected def selectHandler:SelectHandler[QueryResultLike]  = (str,con,q)=>SelectResult.parse(str,q.evaluate())
}