package org.denigma.semantic.controllers

import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/*
controller that can do various readonly quries via reader actor to the database
 */
trait QueryController extends SemanticReader {

  implicit val readTimeout:Timeout = Timeout(5 seconds)

  def defLimit:Long = 50
  def defOffset:Long = 0

  /*
  for test purposes only
   */
  def awaitRead[T](fut:Future[T]):T = Await.result(fut,readTimeout.duration)
}



