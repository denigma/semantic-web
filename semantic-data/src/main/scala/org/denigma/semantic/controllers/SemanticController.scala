package org.denigma.semantic.controllers

import akka.util.Timeout
import scala.concurrent.duration._


/*
makes quries to actors and retrieve results
 */
trait SemanticController extends WithSemanticIO{

  implicit val readTimeout = Timeout(5 seconds)
  implicit val writeTimeout = Timeout(5 seconds)






  //[TOut :ClassTag](query:String,action:AnyQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.anyQuery[TOut](Data.AnyQuery[TOut](query,action,baseUrl))
//  def anyQuery[TOut :ClassTag](data:Data.AnyQuery[TOut]): Future[Try[TOut]] = this.reader.ask(data)(timeout).mapTo[Try[TOut]]

}

/*
  def read[TOut : ClassTag](message:Reading[TOut]): Future[Try[TOut]] = this.read[TOut](Data.Read[TOut](message))
  def read[TOut : ClassTag](data:Data.Read[TOut]): Future[Try[TOut]] = (this.reader.ask(data)(timeout)).mapTo[Try[TOut]]

  def write(action:Writing): Future[Try[Unit]] = this.write(Data.Write(action))
  def write(data:Data.Write): Future[Try[Unit]] = writer.ask(data)(timeout).mapTo[Try[Unit]]

  def select[TOut :ClassTag](query:String,action:TupleQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.select[TOut](Data.Select(query,action,baseUrl))
  def select[TOut :ClassTag](data:Data.Select[TOut]): Future[Try[TOut]] = this.reader.ask(data,timeout).mapTo[Try[TOut]]
  def justSelect(query:String) = this.select[List[Map[String, Value]]](Data.Select(query,this.simpleSelectHandler,WI.RESOURCE))
  protected def simpleSelectHandler(query:String,con: BigdataSailRepositoryConnection, q:BigdataSailTupleQuery): List[Map[String, Value]] =   q.evaluate().toListMap


  def construct[TOut :ClassTag](query:String,action:GraphQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.construct[TOut](Data.Construct(query,action,baseUrl))
  def construct[TOut :ClassTag](data:Data.Construct[TOut]): Future[Try[TOut]] = this.reader.ask(data)(timeout).mapTo[Try[TOut]]
  protected def simpleConstructHandler(query:String,con: BigdataSailRepositoryConnection, q:BigdataSailGraphQuery): List[Statement] =  q.evaluate().toList

  def ask(query:String,action:AskQuering, baseUrl:String = WI.RESOURCE): Future[Try[Boolean]] =  this.ask(Data.Ask(query,action,baseUrl))
  def ask(data:Data.Ask): Future[Try[Boolean]] = this.reader.ask(data)(timeout).mapTo[Try[Boolean]]
  protected def askHandler(query:String,con: BigdataSailRepositoryConnection, q:BigdataSailBooleanQuery) = q.evaluate()

  def update(query:String,action:UpdateQuering, baseUrl:String = WI.RESOURCE): Future[Try[Unit]] =  this.update(Data.Update(query,action,baseUrl))
  def update(data:Data.Update): Future[Try[Unit]] = this.writer.ask(data)(timeout).mapTo[Try[Unit]]
  protected def updateHandler(query:String, con:BigdataSailRepositoryConnection,upd:BigdataSailUpdate): Unit = upd.execute()
  def justUpdate(query:String): Future[Try[Unit]] = this.update(query,this.updateHandler, WI.RESOURCE)


  def aw[T](v:Future[T]): T = Await.result(v,this.timeout.duration)

  def anyQuery[TOut :ClassTag](query:String,action:AnyQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.anyQuery[TOut](Data.AnyQuery[TOut](query,action,baseUrl))
  def anyQuery[TOut :ClassTag](data:Data.AnyQuery[TOut]): Future[Try[TOut]] = this.reader.ask(data)(timeout).mapTo[Try[TOut]]

 */