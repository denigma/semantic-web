package org.denigma.semantic.reading.questions

import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.commons.WI


trait IAsk[T] extends AskReader {
  def question(query:String) = this.askQuery[T](query,askHandler)(WI.RESOURCE)
  protected def askHandler:AskQuerying[T]
}

trait SimpleAsk extends IAsk[Boolean] {
  override protected def askHandler:AskQuerying[Boolean]  = (str,con,q)=>q.evaluate()
}

trait JsonAsk extends IAsk[QueryResultLike] {
  override protected def askHandler:AskQuerying[QueryResultLike]  = (str,con,q)=>AskResult(str,q.evaluate())
}