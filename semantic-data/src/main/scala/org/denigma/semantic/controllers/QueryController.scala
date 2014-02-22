package org.denigma.semantic.controllers

import org.openrdf.query.TupleQueryResult
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
/*
makes quries to actors and retrieve results
 */
trait QueryController extends SemanticController{

  //type Selection= BigdataSailRepositoryConnection=>QueryResult

//  type Selection[T] = Data

  implicit val timeout = Timeout(3 seconds)

//  def tupleQuery(str:String)(mod:QueryModifier = DefaultQueryModifier):Selection = {
//    implicit r=>
//
//      val q= r.prepareTupleQuery(QueryLanguage.SPARQL,str)
//      val res: TupleQueryResult = q.evaluate()
//      QueryResult.parse(str ,res)
//  }
//

  def read[TOut : ClassTag](message:Reading[TOut]): Future[Try[TOut]] = (this.reader ? Data.Read[TOut](message)).mapTo[Try[TOut]]

  def write(action:Writing): Future[Try[Unit]] = (this.writer ? Data.Write).mapTo[Try[Unit]]

  def select[TOut :ClassTag](query:String,action:TupleQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = (this.reader ? Data.Select(query,action,baseUrl)).mapTo[Try[TOut]]

  def construct[TOut :ClassTag](query:String,action:GraphQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = (this.reader ? Data.Construct(query,action,baseUrl)) .mapTo[Try[TOut]]

  def update(query:String,action:UpdateQuering, baseUrl:String = WI.RESOURCE): Future[Try[Unit]] = (this.writer ? Data.Update(query,action,baseUrl)).mapTo[Try[Unit]]

  def aw[T](v:Future[T]): T = Await.result(v,this.timeout.duration)


}
