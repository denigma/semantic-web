package org.denigma.semantic.actors.cache

import org.denigma.semantic.commons.LogLike
import akka.actor.ActorRef
import scala.util.{Success, Failure}
import com.bigdata.rdf.store.AbstractTripleStore
import org.denigma.semantic.actors.cache.Cache.UpdateInfo
import akka.event.EventBus


class ActorChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,sender:ActorRef) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    val f: Failure[UpdateInfo] = Failure[Cache.UpdateInfo](new Exception("transaction aborted"))
    sender ! f
  }

  override def transactionCommited(commitTime: Long): Unit = {
    sender ! Success[Cache.UpdateInfo](this.prepareUpdate())
    this.refresh()
  }
}

//class ChangeEventObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,bus:EventBus) extends ChangeListener(db,transaction,lg)
//{
//  override def transactionAborted(): Unit = {
//    val f: Failure[UpdateInfo] = Failure[Cache.UpdateInfo](new Exception("transaction aborted"))
//    bus.publish(f)
//  }
//
//  override def transactionCommited(commitTime: Long): Unit = {
//    bus.publish(Success[Cache.UpdateInfo](this.prepareUpdate()))
//    this.refresh()
//  }
//}


class ChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,onCompleted:(Cache.UpdateInfo)=>Unit,onAborted:(String)=>Unit) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    this.refresh()
    if(onAborted!=null)
      onAborted(transaction)
  }

  override def transactionCommited(commitTime: Long): Unit = {
    val quads: Cache.UpdateInfo = prepareUpdate()
    this.refresh()
    if(onCompleted!=null)
      onCompleted(quads)

  }
}



