package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.{AkkaLog, NamedActor}
import org.denigma.semantic.writing._
import org.denigma.semantic.commons.LogLike
import java.io.{FileInputStream, File}
import org.openrdf.model.URI
import org.openrdf.rio.RDFFormat
import play.api.libs.json.Json._
import org.denigma.semantic.actors.AkkaLog

/*
class that is responsible for writes into database. It does NOT process read queries
 */
class DatabaseWriter(db:CanWrite) extends  NamedActor with Updater{

  override def receive: Actor.Receive = {
    case Update.Update(query:String)=>sender ! this.update(query)


    case Update.Upload(file:File,contextStr:String)=> this.parseFile(file,contextStr)

    case v=>
        this.log.debug(s"something received by writer $v")
        sender ! v

  }

  override def lg: LogLike = new AkkaLog(this.log)

  override def writeConnection:WriteConnection= db.writeConnection
}
