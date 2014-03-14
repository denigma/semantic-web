package org.denigma.semantic.cache

import org.denigma.semantic.commons.LogLike
import akka.actor.ActorRef
import scala.util.{Success, Failure}
import org.denigma.semantic.writing.WriteConnection
import com.bigdata.rdf.store.AbstractTripleStore


class ActorChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,sender:ActorRef) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    sender ! Failure[UpdateInfo](new Exception("transaction aborted"))
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.refresh()
    sender ! Success[UpdateInfo](this.prepareUpdate())
  }
}


class ChangeObserver(db:AbstractTripleStore,transaction:String="",val lg:LogLike,onCompleted:(UpdateInfo)=>Unit,onAborted:(String)=>Unit) extends ChangeListener(db,transaction,lg)
{
  override def transactionAborted(): Unit = {
    this.refresh()
    if(onAborted!=null)
      onAborted(transaction)
  }

  override def transactionCommited(commitTime: Long): Unit = {
    val quads: UpdateInfo = prepareUpdate()
    this.refresh()
    if(onCompleted!=null)
      onCompleted(quads)

  }
}



