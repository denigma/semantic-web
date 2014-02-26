package org.denigma.semantic.actors.readers

import akka.actor.Actor
import org.denigma.semantic.reading.{ReadConnection, CanRead}
import org.denigma.semantic.actors.{AkkaLog, NamedActor}
import org.denigma.semantic.commons.LogLike

/*
Database actor that works with readonly db connection
 */
class DatabaseReader(db:CanRead) extends  NamedActor with CanRead with JsReader
{

  def receive: Actor.Receive = this.jsonQuery orElse allOther

  def allOther: Actor.Receive = { case v=> this.log.error(s" UNKNOWN message received by reader: $v ") }

  override def lg: LogLike = new AkkaLog(this.log)

  override def readConnection: ReadConnection = db.readConnection
}