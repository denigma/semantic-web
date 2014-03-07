package org.denigma.semantic.controllers

import scala.concurrent.Future
import org.openrdf.model.Value

import scala.util._
import akka.pattern.ask
import org.denigma.semantic.reading._
import org.denigma.semantic.actors.readers.protocols.Read


/*
trait that adds support of quering that returs QueryResultLike (that has toJson method)
under the hood it just sends messages to appropriate actor
 */
trait JsQueryController extends QueryController[QueryResultLike] {


  def query(query:String,offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]]
    = rd(Read.Query(query:String,offset,limit))

  def select(query:String,offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]]
    = rd (Read.Select(query:String,offset,limit))

  def construct(query:String): Future[Try[QueryResultLike]] = rd( Read.Construct(query:String) )

  def question(query:String): Future[Try[QueryResultLike]] = rd (Read.Question(query:String))

  def bindedQuery(query:String,binds:Map[String,String],offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]]
    = rd(Read.Bind(query:String,binds,offset,limit))


  def searchQuery(query:String,binds:Map[String,String],text:String,offset:Long = defOffset, limit:Long = defLimit): Future[Try[QueryResultLike]]
    = rd(Read.Bind(query:String,binds,offset,limit))


}