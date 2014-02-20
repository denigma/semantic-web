package org.denigma.semantic.actors

import org.denigma.semantic.data.RDFStore
import akka.actor.Actor
import scala.util.{Success, Failure}

/*
Database actor that works with readonly db connection
 */
class DatabaseReader(db:RDFStore) extends DBActor(db)
{
  override def receive: Actor.Receive = {

    case  read: Data.Read[_] =>

      sender ! db.readWrite(read.action)


    case v=>this.log.debug(s"something received by reader $v")
  }
}