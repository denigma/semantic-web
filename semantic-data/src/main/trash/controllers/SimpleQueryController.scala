package org.denigma.semantic.controllers

import scala.collection.immutable._
import org.openrdf.query.{GraphQueryResult, TupleQueryResult}
import com.bigdata.rdf.sail.{BigdataSailTupleQuery, BigdataSailGraphQuery, BigdataSailBooleanQuery, BigdataSailRepositoryConnection}
import org.denigma.semantic.controllers.readers.{ConstructController, SelectController, AskController}
import akka.util.Timeout
import scala.concurrent.duration._
import org.denigma.semantic.reading.quries._
import org.denigma.semantic.reading.selections.SelectResult


/*
controller that get native (not mapped) results from queries
 */
trait SimpleQueryController extends
AskController[Boolean]
with SelectController[TupleQueryResult]
with ConstructController[GraphQueryResult]
  with SemanticReader
{
  override val selectHandler:TupleQuering[TupleQueryResult] = _selectHandler _
  override val constructHandler:GraphQuering[GraphQueryResult] = _constructHandler _
  override val askHandler:AskQuering[Boolean] = _askHandler _


  /*
  handler to make  select
  */
  protected def _selectHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailTupleQuery): TupleQueryResult = q.evaluate()

  protected def _constructHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailGraphQuery): GraphQueryResult = q.evaluate()

  protected def _askHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailBooleanQuery): Boolean = q.evaluate()

  override val readTimeout: Timeout = Timeout(5 seconds)
}

