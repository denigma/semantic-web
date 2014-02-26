package org.denigma.semantic.controllers

import scala.concurrent.Future
import scala.util.Try
import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.actors.readers.Read
import org.openrdf.model.Value

import akka.actor.ActorRef
import akka.pattern.ask
import akka.pattern.ask

trait JsQueryController extends QueryController {


  def queryPaginated(query:String,offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Query(query: String, offset, limit), readTimeout).mapTo[Try[QueryResultLike]]
  }

  def query(query:String): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Query(query:String), readTimeout).mapTo[Try[QueryResultLike]]
  }

  def selectPaginated(query:String,offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Select(query:String,offset,limit), readTimeout).mapTo[Try[QueryResultLike]]
  }

  def select(query:String): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Select(query:String), readTimeout).mapTo[Try[QueryResultLike]]
  }

  def construct(query:String): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Construct(query:String), readTimeout).mapTo[Try[QueryResultLike]]
  }


  def question(query:String): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Question(query:String), readTimeout).mapTo[Try[QueryResultLike]]
  }

  def bindedQuery(query:String,params:Map[String,Value]): Future[Try[QueryResultLike]] = {
    reader.ask(Read.Bind(query:String,params), readTimeout ).mapTo[Try[QueryResultLike]]
  }

  def bindedQueryPaginated(query:String,offset:Long = defOffset, limit:Long = defLimit,params:Map[String,Value]): Future[Try[QueryResultLike]] = {
    reader.ask(Read.BindPaginated(query:String,offset,limit,params), readTimeout ).mapTo[Try[QueryResultLike]]
  }

}