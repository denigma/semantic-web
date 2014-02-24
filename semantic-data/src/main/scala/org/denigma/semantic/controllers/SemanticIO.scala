package org.denigma.semantic.controllers

import akka.actor.ActorRef
import scala.reflect.ClassTag
import org.denigma.semantic.data._
import scala.concurrent.Future
import scala.util.Try
import org.denigma.semantic.actors.Data
import akka.pattern.ask
import org.denigma.semantic.controllers.writers.DataWriter
import akka.util.Timeout
import org.denigma.semantic.controllers.readers.DataReader


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



