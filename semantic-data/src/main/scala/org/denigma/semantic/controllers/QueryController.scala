package org.denigma.semantic.controllers

import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import akka.pattern.ask

/*
controller that can do various readonly quries via reader actor to the database
 */
trait QueryController[T] extends WithSemanticReader {

  implicit val readTimeout:Timeout = Timeout(5 seconds)

  def defLimit:Long = 50
  def defOffset:Long = 0

  /*
  for test purposes only
   */
  def awaitRead[TR](fut:Future[TR]):TR = Await.result(fut,readTimeout.duration)


  def rd(message:Any):Future[Try[T]] =
    reader.ask(message)(readTimeout).mapTo[Try[T]]
}



