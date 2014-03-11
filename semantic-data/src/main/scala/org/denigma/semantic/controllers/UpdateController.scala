package org.denigma.semantic.controllers

import akka.util.Timeout
import scala.concurrent.{Await, Future}

import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import org.denigma.semantic.actors.writers.Update
import akka.actor.ActorRef
import akka.pattern.ask
import akka.pattern.ask
import org.denigma.semantic.sparql._
/*
does updates
 */
trait UpdateController extends WithSemanticWriter{

  implicit val writeTimeout:Timeout = Timeout(5 seconds)

  /*
  for test purposes only
   */
  def awaitWrite(fut:Future[Try[Unit]]):Try[Unit] = Await.result(fut,writeTimeout.duration)

  def update(str:String): Future[Try[Unit]] =  this.writer.ask(Update.Update(str))(this.writeTimeout).mapTo[Try[Unit]]

  def insert(ins:InsertQuery): Future[Try[Unit]] =  this.writer.ask(ins)(this.writeTimeout).mapTo[Try[Unit]]
  def delete(del:DeleteQuery): Future[Try[Unit]] =  this.writer.ask(del)(this.writeTimeout).mapTo[Try[Unit]]
  def deleteInsert(q:DeleteInsertQuery): Future[Try[Unit]] =  this.writer.ask(q)(this.writeTimeout).mapTo[Try[Unit]]
  def insertDelete(q:InsertDeleteQuery): Future[Try[Unit]] =  this.writer.ask(q)(this.writeTimeout).mapTo[Try[Unit]]



}
