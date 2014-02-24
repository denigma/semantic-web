package org.denigma.semantic.reading.constructs

import org.openrdf.query.GraphQueryResult
import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.commons.WI

/*
interface that deals with construct quiries
 */
trait IConstruct[T] extends ConstructReader {
  def construct(query:String) = this.graphQuery[T](query,constructHandler)(WI.RESOURCE)
  protected def constructHandler:ConstructQuerying[T]
}

trait SimpleConstruct extends IConstruct[GraphQueryResult] {
  override protected def constructHandler:ConstructQuerying[GraphQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonConstruct extends IConstruct[QueryResultLike] {

  override protected def constructHandler:ConstructQuerying[QueryResultLike] = (str,con,q)=>ConstructResult.parse(str,q.evaluate())
}