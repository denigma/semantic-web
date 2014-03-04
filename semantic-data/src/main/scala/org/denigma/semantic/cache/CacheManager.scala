package org.denigma.semantic.cache

import com.bigdata.rdf.changesets.{IChangeRecord, IChangeLog, InMemChangeLog}
import org.denigma.semantic.commons.{Logged, LogLike}

/*
is used for inmemory data caching
it asumes that it is used in onewriter mode
 */
abstract class CacheManager extends IChangeLog with Logged{

  var consumers:IChangeRecord=>Unit

  override def transactionAborted(): Unit = {
    this.lg.info("some transaction is aborted")
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.lg.info("some transaction is commited")
  }

  override def transactionPrepare(): Unit = {
    this.lg.info("some transaction is prepared")
  }

  override def transactionBegin(): Unit = {
    this.lg.info("some transaction has started")
  }

  override def changeEvent(record: IChangeRecord): Unit = {

  }
}

trait CacheConsumer
{

  def consume(record:IChangeRecord) {


  }

}

