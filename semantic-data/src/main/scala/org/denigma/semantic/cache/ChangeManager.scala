package org.denigma.semantic.cache
import akka.actor.ActorSystem
import scala.concurrent.duration._
import org.denigma.semantic.controllers.UpdateController
import play.api.libs.concurrent.Execution.Implicits._
import org.denigma.semantic.commons.{ChangeWatcher, LogLike}
import com.bigdata.rdf.changesets.IChangeLog


/**
 * Class that deals with caches
 */
object ChangeManager extends ChangeWatcher
{
  var consumers = Set.empty[Consumer]


  def apply(transaction:String,lg:LogLike):  IChangeLog = new ChangeObserver(transaction,lg,committedHandler,abortedHandler)


  def committedHandler(upd:UpdateInfo) = {
    consumers.foreach(c=>c.updateHandler(upd))
  }


  def abortedHandler(transaction:String) = {
    //TODO: complete
  }

  def subscribe(consumer:Consumer) = this.consumers+=consumer
  def unsubscribe(consumer:Consumer) = this.consumers-=consumer





}


trait Consumer{
  def updateHandler(updateInfo:UpdateInfo)
}

