package org.denigma.semantic.actors.cache

import akka.actor.ActorRef
import org.denigma.semantic.commons.ChangeWatcher
import com.bigdata.rdf.store.AbstractTripleStore
import com.bigdata.rdf.changesets.IChangeLog
import akka.event.EventStream
import org.scalax.semweb.commons.LogLike

class CacheWatcher(db: AbstractTripleStore, cache:ActorRef) extends ChangeWatcher{
  override def apply(transaction: String, lg: LogLike): IChangeLog = new ActorChangeObserver(db,transaction,lg,cache)
}

class BusChangeWatcher(db: AbstractTripleStore, bus:EventStream) extends ChangeWatcher{
  override def apply(transaction: String, lg: LogLike): IChangeLog = new EventStreamChangeObserver(db,transaction,lg,bus)
}