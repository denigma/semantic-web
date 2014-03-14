package org.denigma.semantic.writing

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.query.QueryLanguage
import scala.util.Try
import org.denigma.semantic.files.SemanticFileParser
import com.bigdata.rdf.changesets.IChangeLog
import org.denigma.semantic.vocabulary.WI


/**
class that provides updates to the database
 */
trait Updater extends CanWrite  with SemanticFileParser{

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
     if(logger!=null) con.addChangeLog(logger)
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

  def writeConditionalUpdate(condition:String,queryStr:String,update:UpdateHandler,logger:IChangeLog = null)(implicit base:String = WI.RESOURCE): Try[Boolean] = {
    val con: BigdataSailRepositoryConnection = this.writeConnection
    con.setAutoCommit(false)
    val res: Try[Boolean] = Try{
      val toWrite: Boolean = con.prepareBooleanQuery(QueryLanguage.SPARQL,condition).evaluate()
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
      lg.error(s"UPDATE query \n $queryStr \nfailed because of \n"+e.getMessage)
      res
    }

  }



  def updateHandler:UpdateHandler = (query,con,upd)=> upd.execute()

  def update(query:String,logger:IChangeLog = null): Try[Unit] = this.writeUpdate(query,updateHandler,logger)

  def conditionalUpdate(condition:String,query:String,logger:IChangeLog = null): Try[Boolean] = this.writeConditionalUpdate(condition,query,updateHandler,logger)




}
