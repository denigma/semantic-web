package org.denigma.semantic.actors.cache

import akka.actor.ActorRef
import com.bigdata.rdf.store.AbstractTripleStore
import akka.event.EventStream
import org.scalax.semweb.sesame.LogLike


class ActorChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,sender:ActorRef) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    //val f: Failure[UpdateInfo] = Failure[Cache.UpdateInfo](new Exception("transaction aborted"))
    sender ! Cache.failed(transaction)
    this.refresh()
  }

  override def transactionCommited(commitTime: Long): Unit = {
    val upd = this.prepareUpdate()
    lg.debug("transaction commited "+transaction+" : "+upd.toString)
    sender ! upd
    this.refresh()
  }
}

class EventStreamChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,bus:EventStream) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    //val f: Failure[UpdateInfo] = Failure[Cache.UpdateInfo](new Exception("transaction aborted"))
    bus.publish(Cache.failed(transaction))
    //sender ! Cache.failed(transaction)
    this.refresh()
  }

  override def transactionCommited(commitTime: Long): Unit = {
    val upd = this.prepareUpdate()
    lg.debug("transaction commited "+transaction+" : "+upd.toString)
    //sender ! upd
    bus.publish(upd)
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



