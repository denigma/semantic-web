package org.denigma.semantic.controllers.readers

import scala.reflect.ClassTag
import org.denigma.semantic.data._
import scala.concurrent.Future
import scala.util.Try
import org.denigma.semantic.actors.Data
import akka.pattern.ask
import org.denigma.semantic.commons.WI

//trait SimpleSelector[T] extends SemanticReader {
//  def select[TOut :ClassTag](query:String,action:TupleQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.select[TOut](Data.Select(query,action,baseUrl))
//  def select[TOut :ClassTag](data:Data.Select[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]
//}
//
//trait Asker extends SemanticReader {
//  /*
//  Sends ask query to get True or False
//   */
//  def askQuery(query:String,action:AskQuering[Boolean], baseUrl:String = WI.RESOURCE): Future[Try[Boolean]] =  this.askQuery[Boolean](Data.Ask(query,action,baseUrl))
//  def askQuery[TOut :ClassTag](query:String,action:AskQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] =  this.askQuery[TOut](Data.Ask(query,action,baseUrl))
//  def askQuery[TOut :ClassTag](data:Data.Ask[TOut]): Future[Try[TOut]] = this.reader.ask(data)(readTimeout).mapTo[Try[TOut]]
//
//}
//
//trait Constructor extends SemanticReader {
//
//  def construct[TOut :ClassTag](query:String,action:GraphQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.construct[TOut](Data.Construct(query,action,baseUrl))
//  def construct[TOut :ClassTag](data:Data.Construct[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]
//
//}
//
//trait AnyQueryController extends SemanticReader {
//
//  def anyQuery[TOut :ClassTag](query:String,action:AnyQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.anyQuery[TOut](Data.AnyQuery[TOut](query,action,baseUrl))
//  def anyQuery[TOut :ClassTag](data:Data.AnyQuery[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]
//
//
//}