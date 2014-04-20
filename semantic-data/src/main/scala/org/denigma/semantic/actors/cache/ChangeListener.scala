package org.denigma.semantic.actors.cache

import com.bigdata.rdf.spo.ISPO
import org.denigma.semantic.commons.{Logged, LogLike}
import com.bigdata.rdf.changesets.{ChangeAction, IChangeRecord, IChangeLog}
import com.bigdata.rdf.store.AbstractTripleStore
import com.bigdata.striterator.ChunkedArrayIterator
import org.denigma.rdf._
import org.denigma.semantic.sesame
import org.denigma.semantic.sesame._
import org.denigma.rdf.model.Quad


/*
is used for inmemory data caching
it asumes that it is used in onewriter mode
 */
abstract class ChangeListener(db:AbstractTripleStore,transaction:String, lg:LogLike) extends IChangeLog with Logged{

  var removed = Set.empty[ISPO]
  var inserted = Set.empty[ISPO]
  var inferred = Set.empty[ISPO]



  override def transactionAborted(): Unit = {
    this.lg.error(s"ABORTED transaction: \n $transaction")
  }

  override def transactionCommited(commitTime: Long): Unit = {
    this.lg.info(s"COMMITTED transaction: \n $transaction")
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

  def prepareUpdate(): Cache.UpdateInfo =  {
    Cache.UpdateInfo(transaction,this.resolve(inserted),this.resolve(removed),this.resolve(inferred))
  }


  /**
   * Cleans ses for accumulation
   */
  def refresh(): Unit = {
    removed = Set.empty[ISPO]
    inserted = Set.empty[ISPO]
    inferred = Set.empty[ISPO]
  }


  /**
   * Batch extracts statements from the database
   * @param coll
   * @return
   */
  def resolve(coll:Set[ISPO]): Set[Quad] = {
    if(coll.size==0) return Set.empty[Quad]
    val arr = coll.toArray
    val src = new ChunkedArrayIterator[ISPO](arr)
    db.asStatementIterator(src).collect{case st if st!=null=>sesame.Statement2Quad(st)}.toSet[Quad]
    //if(db==null) this.lg.error("NULL DB")
    //coll.map(Quad(_)).toSet
  }




}