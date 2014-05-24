package org.denigma.semantic.actors.readers

import akka.actor.Actor
import org.denigma.semantic.actors.{WatchProtocol, AkkaLog, NamedActor}
import org.denigma.semantic.reading.CanReadBigData
import org.scalax.semweb.sesame.LogLike

/**
 * Database actor that works with readonly db connection
 * @param db anything that can provide read connection
 */
class DatabaseReader(db:CanReadBigData) extends  NamedActor with CanReadBigData with JsReader with SimpleReader with PatternReader
{


  def receive: Actor.Receive = jsonQuery orElse simpleQuery orElse patternRead orElse  allOther

  def allOther: Actor.Receive = {

    case v=>

      this.log.error(s" UNKNOWN message received by reader: $v ") }

  override def lg: LogLike = new AkkaLog(this.log)

  override def readConnection: ReadConnection = db.readConnection
}