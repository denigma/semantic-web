package org.denigma.semantic.actors.readers

import akka.actor.Actor
import org.denigma.semantic.actors.{AkkaLog, NamedActor}
import org.denigma.semantic.reading.CanReadBigData
import org.scalax.semweb.commons.LogLike
import org.scalax.semweb.sesame.shapes.ShapeReader

/**
 * Database actor that works with readonly db connection
 * @param db anything that can provide read connection
 */
class DatabaseReader(db:CanReadBigData) extends  NamedActor
with CanReadBigData
with JsReader
with SimpleReader
with PatternReader
with ShapeReader
with ShExReader
{


  def receive: Actor.Receive = jsonQuery orElse simpleQuery orElse patternRead orElse shapeRead orElse  allOther

  def allOther: Actor.Receive = {

    case v=>

      this.log.error(s" UNKNOWN message received by reader: $v ") }

  override def lg: LogLike = new AkkaLog(this.log)

  override def readConnection: ReadConnection = db.readConnection


}