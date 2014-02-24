package org.denigma.semantic.controllers.readers

import org.denigma.semantic.quering.QueryResultLike
import com.bigdata.rdf.sail.{BigdataSailBooleanQuery, BigdataSailGraphQuery, BigdataSailTupleQuery, BigdataSailRepositoryConnection}
import akka.util.Timeout
import akka.actor.ActorRef
import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.reading.selections.SelectResult

/*
returns results in JSON format, useful for s
 */
trait JsonQueryController extends AnyQueryController[QueryResultLike]{
  /*
  handler to make  select
  */
  val selectHandler = _selectHandler _
  protected def _selectHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailTupleQuery): QueryResultLike = SelectResult.parse(query,q.evaluate())

  val constructHandler = _constructHandler _
  protected def _constructHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailGraphQuery): QueryResultLike = SelectResult.parse(query,q.evaluate())

  val askHandler = _askHandler _
  protected def _askHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailBooleanQuery): QueryResultLike = SelectResult.parse(query,q.evaluate())
}
