package org.denigma.semantic.cache

import com.bigdata.rdf.changesets._
import org.denigma.semantic.commons.LogLike
import com.bigdata.rdf.store.BigdataStatementIterator
import com.bigdata.rdf.model.BigdataStatement
import com.bigdata.rdf.spo.ISPO
import com.bigdata.striterator.ChunkedArrayIterator
import org.denigma.semantic.writing.DataWriter
import com.bigdata.rdf.lexicon.LexiconRelation
import org.denigma.semantic.sparql.Trip
import akka.actor.ActorRef
import scala.util.{Success, Failure, Try}


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
    if(onAborted!=null) onAborted(transaction)
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.refresh()
    if(onCompleted!=null)onCompleted(prepareUpdate())
  }
}



