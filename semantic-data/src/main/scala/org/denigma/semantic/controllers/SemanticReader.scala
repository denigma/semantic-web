package org.denigma.semantic.controllers

import akka.actor.ActorRef


object SemanticReader {
  def readingEnabled = this._reader!=null

  protected var _reader:ActorRef = null
  def reader = _reader
  def reader_=(value:ActorRef): Unit = this._reader = value

}
/*
should be mixed in classes that want to send readonly requests
 */
trait WithSemanticReader extends SemanticReaderLike{
  override def reader: ActorRef = SemanticReader.reader
}

trait SemanticReaderLike {
  def reader: ActorRef

}