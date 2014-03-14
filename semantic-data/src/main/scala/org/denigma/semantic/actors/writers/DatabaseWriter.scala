package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing._
import org.denigma.semantic.commons.{ChangeWatcher, LogLike}
import java.io.File
import org.denigma.semantic.actors.AkkaLog
import org.denigma.semantic.sparql._
import scala.util.Try
import com.bigdata.rdf.changesets.IChangeLog

/**
class that is responsible for writes into database. It does NOT process read queries
@param writer just an object that can provide WriteConnection, can be db, can be anything else
 */
class DatabaseWriter(writer:CanWrite, val watcher:ChangeWatcher) extends  WatchedWriter{


  override def receive: Actor.Receive = {



    case Update.Update(query:String)=>sender ! this.watchedUpdate(query)

    case Update.Upload(file:File,contextStr:String)=> sender ! this.parseFile(file,contextStr)

    case InsertQuery(ins) => sender ! this.watchedUpdate(ins.stringValue)

    case ConditionalInsertQuery(question,insert)=> sender ! this.watchedConditionalUpdate(question.stringValue,insert.stringValue)

    case DeleteQuery(del) =>  sender ! this.watchedUpdate(del.stringValue)

    case ConditionalDeleteQuery(question,delete)=> sender ! this.watchedConditionalUpdate(question.stringValue,delete.stringValue)

    case InsertDeleteQuery(i,d) => sender ! this.watchedUpdate(i.stringValue+" \n"+d.stringValue)

    case ConditionalInsertDeleteQuery(question,i,d)=> sender ! this.watchedConditionalUpdate(question.stringValue,i.stringValue+" \n"+d.stringValue)

    case DeleteInsertQuery(d,i) => sender ! this.watchedUpdate(d.stringValue+" \n"+i.stringValue)

    case ConditionalDeleteInsertQuery(question,d,i)=> sender ! this.watchedConditionalUpdate(question.stringValue,d.stringValue+" \n"+i.stringValue)



    case v=>
        this.log.error(s"something strange received by writer: \n $v")
        sender ! v

  }

  override def lg: LogLike = new AkkaLog(this.log)

  override def writeConnection:WriteConnection= writer.writeConnection

  override def db = writer.db


  def watchedUpdate(queryString:String): Try[Unit] = this.update(queryString,watcher(this.db,queryString,lg))

  def watchedConditionalUpdate(condition:String,queryString:String): Try[Boolean] = this.conditionalUpdate(condition,queryString,watcher(this.db,queryString,lg))


}


trait WatchedWriter extends NamedActor with Updater{

  val watcher:ChangeWatcher

  def watchedUpdate(queryString:String): Try[Unit]

  def watchedConditionalUpdate(condition:String,queryString:String): Try[Boolean]
}
