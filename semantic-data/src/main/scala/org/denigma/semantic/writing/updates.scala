package org.denigma.semantic.writing


import com.bigdata.rdf.sail.{BigdataSailUpdate, BigdataSailRepositoryConnection}
import org.scalax.semweb.sesame.{CanWriteSesame, SesameDataWriter}
/**
Trait that can provide writeConnection. It is used everywhere where we need to write something into the database
  */
trait CanWriteBigData extends CanWriteSesame{

  type WriteConnection  = BigdataSailRepositoryConnection
}
/*
interface for data writing
 */
trait DataWriter extends SesameDataWriter with CanWriteBigData


import com.bigdata.rdf.changesets.IChangeLog
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.files.SemanticFileParser
import org.openrdf.query.QueryLanguage
import org.scalax.semweb.rdf.vocabulary.WI

import scala.util.Try


/**
class that provides updates to the database
  */
trait Updater extends CanWriteBigData  with SemanticFileParser
{

  type UpdateQuery = BigdataSailUpdate
  type UpdateHandler = (String,WriteConnection,UpdateQuery)=>Unit

  /**
   * Writed update to the database
   * @param queryStr query string
   * @param update updateHandler
   * @param logger logger that will monitor changes
   * @param base basic URL (needed by BigData)
   * @return Success or Failure
   */
  def writeUpdate(queryStr:String,update:UpdateHandler,logger:IChangeLog = null)(implicit base:String = WI.RESOURCE) = {
    val con: BigdataSailRepositoryConnection = this.writeConnection
    con.setAutoCommit(false)
    val res = Try{
      if(logger!=null) {
        con.addChangeLog(logger)
        //lg.debug("WATCHED UPDATE WITH: "+queryStr)
      }
      else {
        //lg.error("UNWATCHED UPDATE WITH: "+queryStr)
      }
      val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,queryStr,base)
      update(queryStr,con,u)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"UPDATE query \n $queryStr \nfailed because of \n"+e.getMessage)
      res
    }

  }

  /**
   * Updates based on results of evaluation of ask query
   * @param queryStr  update query
   * @param condition condition   query
   * @param update update handler
   * @param negation if condition is negated
   * @param logger logger to log result
   * @param base basic IRI (bigdata requires but I have no clue why it is needed)
   * @return
   */
  def writeConditionalUpdate(queryStr:String,condition:String,update:UpdateHandler,negation:Boolean=false,logger:IChangeLog = null)(implicit base:String = WI.RESOURCE): Try[Boolean] = {
    val con: BigdataSailRepositoryConnection = this.writeConnection
    con.setAutoCommit(false)
    val res: Try[Boolean] = Try{
      val cond: Boolean = con.prepareBooleanQuery(QueryLanguage.SPARQL,condition).evaluate()
      val toWrite = if(negation) !cond else cond
      if(toWrite)
      {
        if(logger!=null) con.addChangeLog(logger)
        val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,queryStr,base)
        update(queryStr,con,u)
      }
      toWrite
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"Conditional UPDATE query with condition: \n$condition\n and query string: \n $queryStr \nfailed because of \n"+e.getMessage)
      res
    }

  }


  /*
 writes something and then closes the connection
  */
  def watchedWrite[T](logger:IChangeLog )(action:WriteConnection=>T):Try[T] =
  {
    val con = this.writeConnection
    con.setAutoCommit(false)
    val res = Try {
      con.addChangeLog(logger)
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }



  def updateHandler:UpdateHandler = (query,con,upd)=> upd.execute()

  def update(query:String,logger:IChangeLog = null): Try[Unit] = this.writeUpdate(query,updateHandler,logger)

  def updateOnlyIf(query:String,condition:String,logger:IChangeLog = null): Try[Boolean] = this.writeConditionalUpdate(query,condition,updateHandler,negation = false,logger)

  /**
   * Updates RDF storage if the condition does not hold
   * @param query update query
   * @param condition condition to be checked
   * @param logger logger
   * @return try of true/false
   */
  def updateUnless(query:String,condition:String,logger:IChangeLog = null): Try[Boolean] = this.writeConditionalUpdate(query,condition,updateHandler,negation = true,logger)




}
