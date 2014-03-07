package org.denigma.semantic.cache

import com.bigdata.rdf.spo.ISPO
import org.denigma.semantic.commons.{Logged, LogLike}
import com.bigdata.rdf.changesets.{InMemChangeLog, ChangeAction, IChangeRecord, IChangeLog}


case class UpdateInfo(transaction:String,inserted:Set[ISPO],removed:Set[ISPO] = Set.empty,inferred:Set[ISPO] = Set.empty)


trait UpdateInfoLike{

  def removed: Set[ISPO]
  def inserted: Set[ISPO]
  def inferred: Set[ISPO]

}

/*
is used for inmemory data caching
it asumes that it is used in onewriter mode
 */
abstract class ChangeListener(transaction:String,val lg:LogLike) extends IChangeLog with Logged with UpdateInfoLike{

  var removed = Set.empty[ISPO]
  var inserted = Set.empty[ISPO]
  var inferred = Set.empty[ISPO]


  override def transactionAborted(): Unit = {
    this.lg.error(s"ABORTED transaction: \n ${transaction}")
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.lg.info(s"COMMITED transaction: \n ${transaction}")
  }

  override def transactionPrepare(): Unit = {
    // this.lg.info("some transaction is prepared")
  }

  override def transactionBegin(): Unit = {
    //this.lg.info("some transaction has started")
  }

  override def changeEvent(record: IChangeRecord): Unit = {
    record.getChangeAction match {
      case ChangeAction.INSERTED => inserted+=record.getStatement

      case ChangeAction.REMOVED => removed +=record.getStatement

      case ChangeAction.UPDATED => inferred +=record.getStatement

    }

  }

  def prepareUpdate(): UpdateInfo =  UpdateInfo(transaction,inserted,removed,inferred)


  def refresh() = {
    removed = Set.empty[ISPO]
    inserted = Set.empty[ISPO]
    inferred = Set.empty[ISPO]
  }

}