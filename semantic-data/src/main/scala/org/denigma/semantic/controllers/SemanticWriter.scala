package org.denigma.semantic.controllers

import akka.actor.ActorRef

object SemanticWriter {

  protected var _writer:ActorRef = null
  def writer = _writer
  def writer_=(value:ActorRef): Unit = this._writer = value

  def writingEnabled = this._writer!=null

}



trait WithSemanticWriter extends SemanticWriterLike {
  override def writer: ActorRef = SemanticWriter.writer
}


trait SemanticWriterLike {
  def writer: ActorRef
}





