package org.denigma.semantic.controllers

import org.openrdf.query.{QueryLanguage, TupleQueryResult}
import org.denigma.semantic.actors._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import org.denigma.semantic.data._
import scala.reflect.ClassTag
import org.denigma.semantic.WI
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.duration.Duration._
import org.denigma.semantic.quering.{QueryResult, DefaultQueryModifier, QueryModifier}
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

/*
makes quries to actors and retrieve results
 */
trait SemanticController extends WithSemanticIO{

  implicit val timeout = Timeout(5 seconds)

//  type Selection[T] = Data


  def read[TOut : ClassTag](message:Reading[TOut]): Future[Try[TOut]] = this.read[TOut](Data.Read[TOut](message))
  def read[TOut : ClassTag](data:Data.Read[TOut]): Future[Try[TOut]] = (this.reader ? data)(timeout).mapTo[Try[TOut]]

  def write(action:Writing): Future[Try[Unit]] = this.write(Data.Write(action))
  def write(data:Data.Write): Future[Try[Unit]] = (this.writer ? data)(timeout).mapTo[Try[Unit]]

  def select[TOut :ClassTag](query:String,action:TupleQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.select[TOut](Data.Select(query,action,baseUrl))
  def select[TOut :ClassTag](data:Data.Select[TOut]): Future[Try[TOut]] = (this.reader ? data)(timeout).mapTo[Try[TOut]]

  def construct[TOut :ClassTag](query:String,action:GraphQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.construct[TOut](Data.Construct(query,action,baseUrl))
  def construct[TOut :ClassTag](data:Data.Construct[TOut]): Future[Try[TOut]] = (this.reader ? data)(timeout).mapTo[Try[TOut]]

  def update(query:String,action:UpdateQuering, baseUrl:String = WI.RESOURCE): Future[Try[Unit]] =  this.update(Data.Update(query,action,baseUrl))
  def update(data:Data.Update): Future[Try[Unit]] = (this.writer ? data)(timeout).mapTo[Try[Unit]]

  def aw[T](v:Future[T]): T = Await.result(v,this.timeout.duration)

  def anyQuery[TOut :ClassTag](query:String,action:AnyQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.anyQuery[TOut](Data.AnyQuery[TOut](query,action,baseUrl))
  def anyQuery[TOut :ClassTag](data:Data.AnyQuery[TOut]): Future[Try[TOut]] = (this.reader ? data)(timeout).mapTo[Try[TOut]]


}
