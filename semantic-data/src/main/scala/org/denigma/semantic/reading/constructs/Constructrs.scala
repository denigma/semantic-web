package org.denigma.semantic.reading.constructs

import org.openrdf.query.GraphQueryResult
import org.denigma.semantic.reading.QueryResultLike
import org.scalax.semweb.rdf.vocabulary.WI


/*
interface that deals with construct quiries
 */
trait IConstruct[T] extends ConstructReader {
  def construct(query:String) = this.graphQuery[T](query,constructHandler)(WI.RESOURCE)
  protected def constructHandler:ConstructHandler[T]
}

trait SimpleConstruct extends IConstruct[GraphQueryResult] {
  override protected def constructHandler:ConstructHandler[GraphQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonConstruct extends IConstruct[QueryResultLike] {

  override protected def constructHandler:ConstructHandler[QueryResultLike] = (str,con,q)=>ConstructResult.parse(str,q.evaluate())
}