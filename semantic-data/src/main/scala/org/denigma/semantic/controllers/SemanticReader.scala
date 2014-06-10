package org.denigma.semantic.controllers

import akka.actor.ActorRef
import org.scalax.semweb.commons.{LogLike, Logged}


object SemanticReader {
  def readingEnabled = this._reader!=null

  protected var _reader:ActorRef = null
  def reader = _reader
  def reader_=(value:ActorRef): Unit = this._reader = value

  protected var _cache:ActorRef = null
  def cache = _cache
  def cache_=(value:ActorRef): Unit = this._cache = value

  def cacheEnabled = this._cache!=null

}
/*
should be mixed in classes that want to send readonly requests
 */
trait WithSemanticReader extends SemanticReaderLike{
  override def reader: ActorRef = SemanticReader.reader
  override def cache:ActorRef = SemanticReader.cache
}

object LoggerProvider extends Logged
{
  protected var _lg:LogLike = null
  def lg:LogLike = _lg
  def lg_=(value:LogLike): Unit = this._lg = value
}

trait WithLogger extends Logged {
  def lg:LogLike = LoggerProvider.lg
}

trait SemanticReaderLike {
  def reader: ActorRef
  def cache:ActorRef

}
