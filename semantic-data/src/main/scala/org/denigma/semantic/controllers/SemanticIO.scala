package org.denigma.semantic.controllers

import akka.actor.ActorRef





/*
just a trait that has reader and writer
 */
trait SemanticIOLike{

  def reader:ActorRef

  def writer:ActorRef

  def valid:Boolean=  reader!=null && writer!=null

}

trait DataReader {
  def reader: ActorRef

}

trait DataWriter {
  def writer: ActorRef
}





