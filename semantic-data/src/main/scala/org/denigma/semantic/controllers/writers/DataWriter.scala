package org.denigma.semantic.controllers.writers

import org.denigma.semantic.data._
import scala.concurrent.{Await, Future}
import scala.util.Try
import org.denigma.semantic.actors.Data
import scala.reflect.ClassTag

import akka.actor.ActorRef
import scala.reflect.ClassTag
import org.denigma.semantic.data._
import scala.util.Try
import org.denigma.semantic.actors.Data
import akka.pattern.ask
import org.denigma.semantic.WI

/*
trait that sends write operations to actors
 */
trait DataWriter {
  def writer:ActorRef

  val writeTimeout:akka.util.Timeout

  //def write(action:Writing[Unit]): Future[Try[Unit]] = this.write[Unit](Data.Write[Unit](action))
  def write[TOut](action:Writing[TOut]): Future[Try[TOut]] = this.write[TOut](Data.Write[TOut](action))
  def write[TOut](data:Data.Write[TOut]): Future[Try[TOut]] = (this.writer ? data)(writeTimeout).mapTo[Try[TOut]]

  /*
  BLOCKING! mostly for testing purposes
 */
  def awaitWrite[T](v:Future[T]): T = Await.result(v,this.writeTimeout.duration)

}

trait Updater extends DataWriter {

  //def update(query:String,action:UpdateQuering[Unit], baseUrl:String = WI.RESOURCE): Future[Try[Unit]] =  this.update[Unit](Data.Update(query,action,baseUrl))
  def update[TOut](query:String,action:UpdateQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] =  this.update[TOut](Data.Update(query,action,baseUrl))
  def update[TOut](data:Data.Update[TOut]): Future[Try[TOut]] = (this.writer ? data)(writeTimeout).mapTo[Try[TOut]]

}