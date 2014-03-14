package org.denigma.semantic.cache

import com.bigdata.rdf.spo.ISPO
import org.denigma.semantic.commons.{Logged, LogLike}
import com.bigdata.rdf.changesets.{InMemChangeLog, ChangeAction, IChangeRecord, IChangeLog}
import org.denigma.semantic.model.Quad
import com.bigdata.rdf.store.{BigdataStatementIterator, AbstractTripleStore}
import com.bigdata.striterator.ChunkedArrayIterator
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.cache._
import com.bigdata.rdf.model.BigdataStatement


case class UpdateInfo(transaction:String,inserted:Set[Quad],removed:Set[Quad] = Set.empty,inferred:Set[Quad] = Set.empty)


trait UpdateInfoLike{

  def removed: Set[Quad]
  def inserted: Set[Quad]
  def inferred: Set[Quad]

}

/*
is used for inmemory data caching
it asumes that it is used in onewriter mode
 */
abstract class ChangeListener(db:AbstractTripleStore,transaction:String, lg:LogLike) extends IChangeLog with Logged{

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

  def prepareUpdate(): UpdateInfo =  {
    UpdateInfo(transaction,this.resolve(inserted),this.resolve(removed),this.resolve(inferred))
  }


  def refresh() = {
    removed = Set.empty[ISPO]
    inserted = Set.empty[ISPO]
    inferred = Set.empty[ISPO]
  }


  def resolve(coll:Set[ISPO]): Set[Quad] = {
    if(coll.size==0) return Set.empty[Quad]
    val arr = coll.toArray
    val src = new ChunkedArrayIterator[ISPO](arr)
    db.asStatementIterator(src).filter(_!=null).map(Quad(_)).toSet[Quad]
    //if(db==null) this.lg.error("NULL DB")
    //coll.map(Quad(_)).toSet
  }




}