package org.denigma.semantic.controllers

import akka.actor.ActorRef


/*
object that contains reader and writer
 */
object SemanticIO extends SemanticIOLike{

  protected var _reader:ActorRef = null

  protected var _writer:ActorRef = null

  def reader = _reader

  def writer = _writer

  /*
  Inits reader and writer actorref
   */
  def init(read:ActorRef,write:ActorRef) = {
    this._reader = read
    this._writer = write
    this
  }

}

trait SemanticIOLike{

  def reader:ActorRef

  def writer:ActorRef

  def valid:Boolean=  reader!=null && writer!=null

}

trait WithSemanticIO extends SemanticIOLike{
  override def writer: ActorRef = SemanticIO.writer

  override def reader: ActorRef = SemanticIO.reader
}