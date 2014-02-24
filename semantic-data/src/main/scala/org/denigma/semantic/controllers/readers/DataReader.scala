package org.denigma.semantic.controllers.readers

import scala.reflect.ClassTag
import org.denigma.semantic.data._
import scala.concurrent.{Await, Future}
import scala.util.Try
import org.denigma.semantic.actors.Data
import org.denigma.semantic.controllers.SemanticIOLike
import akka.actor.ActorRef
import akka.pattern.ask
import reflect.ClassTag

/*
trait that sends read messages to actors
 */
trait DataReader {

  def reader:ActorRef

  val readTimeout:akka.util.Timeout

  def read[TOut](message:Reading[TOut]): Future[Try[TOut]] = this.read[TOut](Data.Read[TOut](message))
  def read[TOut](data:Data.Read[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]

  /*
  mostly for testing purposes
   */
  def awaitRead[T](v:Future[T]): T = Await.result(v,this.readTimeout.duration)



}