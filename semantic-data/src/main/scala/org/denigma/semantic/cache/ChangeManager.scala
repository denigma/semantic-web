package org.denigma.semantic.cache
import akka.actor.ActorSystem
import scala.concurrent.duration._
import org.denigma.semantic.controllers.{WithLogger, UpdateController}
import play.api.libs.concurrent.Execution.Implicits._
import org.denigma.semantic.commons.{ChangeWatcher, LogLike}
import com.bigdata.rdf.changesets.IChangeLog
import org.denigma.semantic.writing.WriteConnection
import com.bigdata.rdf.store.AbstractTripleStore


/**
 * Class that deals with caches
 */
object ChangeManager extends ChangeWatcher with WithLogger
{
  var consumers = Set.empty[Consumer]


  def apply(db:AbstractTripleStore,transaction:String,lg:LogLike):  IChangeLog = new ChangeObserver(db,transaction,lg,committedHandler,abortedHandler)


  def committedHandler(upd:UpdateInfo): Unit = {

    this.consumers.foreach(c=>c.updateHandler(upd))
  }


  def abortedHandler(transaction:String) = {
    lg.error(s"TRANSACTION $transaction ABORTED")
  }

  def subscribe(consumer:Consumer) = this.consumers = this.consumers + consumer
  def unsubscribe(consumer:Consumer) = this.consumers = this.consumers - consumer





}


trait Consumer{
  def updateHandler(updateInfo:UpdateInfo)
}

