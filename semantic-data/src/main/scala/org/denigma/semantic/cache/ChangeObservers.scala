package org.denigma.semantic.cache

import org.denigma.semantic.commons.LogLike
import akka.actor.ActorRef
import scala.util.{Success, Failure}


class ActorChangeObserver(transaction:String="",lg:LogLike,sender:ActorRef) extends ChangeListener(transaction,lg)
{
  override def transactionAborted(): Unit = {
    sender ! Failure[UpdateInfo](new Exception("transaction aborted"))
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.refresh()
    sender ! Success[UpdateInfo](this.prepareUpdate())
  }
}

/**
 * Adds handlers
 * @param transaction transaction that is done with this observer
 * @param lg logger
 * @param onCompleted on completed handler
 * @param onAborted onAborted handler
 */
class ChangeObserver(transaction:String="",lg:LogLike,onCompleted:(UpdateInfo)=>Unit,onAborted:(String)=>Unit) extends ChangeListener(transaction,lg)
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



