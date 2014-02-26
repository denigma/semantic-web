package org.denigma.semantic.controllers

import akka.actor.ActorRef
import org.denigma.semantic.controllers.DataWriter
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

trait SemanticWriter extends DataWriter{
  def writer: ActorRef = SemanticIO.writer
}

trait SemanticReader extends DataReader{


  def reader: ActorRef = SemanticIO.reader
}

trait WithSemanticIO extends SemanticIOLike{
  override def writer: ActorRef = SemanticIO.writer

  override def reader: ActorRef = SemanticIO.reader

}