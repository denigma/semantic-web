package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing._
import org.denigma.semantic.commons.LogLike
import java.io.File
import org.denigma.semantic.actors.AkkaLog
import org.denigma.semantic.sparql._

/**
class that is responsible for writes into database. It does NOT process read queries
@param db just an object that can provide WriteConnection, can be db, can be anything else
 */
class DatabaseWriter(db:CanWrite) extends  NamedActor with Updater{

  override def receive: Actor.Receive = {
    case Update.Update(query:String)=>sender ! this.update(query)


    case Update.Upload(file:File,contextStr:String)=> this.parseFile(file,contextStr)


    case InsertQuery(q) => sender ! this.update(q.stringValue)

    case DeleteQuery(q) => sender ! this.update(q.stringValue)

    case InsertDeleteQuery(i,d) => sender ! this.update(i.stringValue+" \n"+d.stringValue)

    case DeleteInsertQuery(d,i) => sender ! this.update(d.stringValue+" \n"+i.stringValue)


    case v=>
        this.log.debug(s"something received by writer $v")
        sender ! v

  }

  override def lg: LogLike = new AkkaLog(this.log)

  override def writeConnection:WriteConnection= db.writeConnection
}
