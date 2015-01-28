package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing._
import org.denigma.semantic.commons.{ChangeWatcher}
import java.io.File
import scala.util.Try
import org.denigma.semantic.actors.AkkaLog
import org.scalax.semweb.sparql._
import org.scalax.semweb.commons.LogLike


/**
class that is responsible for writes into database. It does NOT process read queries
@param writer just an object that can provide WriteConnection, can be db, can be anything else
 */
class DatabaseWriter(writer:CanWriteBigData, val watcher:ChangeWatcher) extends
WatchedWriter with ConditionalWriter with GridWriter
{


  override def receive: Actor.Receive = this.simpleUpdates
    .orElse(this.updatesOnlyIf)
    .orElse(this.updatesUnless)
    .orElse(this.updateShape)
    .orElse(this.updateGrid)
    .orElse {

    case v=>
        this.log.error(s"something strange received by writer: \n $v")
        sender ! v

  }



  def simpleUpdates:Actor.Receive = {
    case Update.Update(query:String)=>sender ! this.watchedUpdate(query)

    case Update.Upload(file:File,contextStr:String)=> sender ! this.parseFile(file,contextStr)

    case InsertQuery(ins) => sender ! this.watchedUpdate(ins.stringValue)

    case DeleteQuery(del) =>  sender ! this.watchedUpdate(del.stringValue)

    case InsertDeleteQuery(i,d) => sender ! this.watchedUpdate(i.stringValue+" \n"+d.stringValue)

    case DeleteInsertQuery(d,i) => sender ! this.watchedUpdate(d.stringValue+" \n"+i.stringValue)

  }


  override def lg: LogLike = new AkkaLog(this.log)

  override def writeConnection:WriteConnection= writer.writeConnection


  def watchedUpdate(queryString:String): Try[Unit] = this.update(queryString,watcher(queryString,lg))



}


trait WatchedWriter extends NamedActor with Updater{

  val watcher:ChangeWatcher

  def watchedUpdate(queryString:String): Try[Unit]

}
