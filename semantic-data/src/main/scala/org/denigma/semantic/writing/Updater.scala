package org.denigma.semantic.writing

import org.denigma.semantic.commons.WI
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.query.QueryLanguage
import scala.util.Try
import org.denigma.semantic.files.SemanticFileParser
import com.bigdata.rdf.changesets.IChangeLog


/**
class that provides updates to the database
 */
trait Updater extends CanWrite  with SemanticFileParser{

  /**
   * Writed update to the database
   * @param str query string
   * @param update updateHandler
   * @param logger logger that will monitor changes
   * @param base basic URL (needed by BigData)
   * @return Success or Failure
   */
  def writeUpdate(str:String,update:UpdateHandler,logger:IChangeLog = null)(implicit base:String = WI.RESOURCE) = {
    val con: BigdataSailRepositoryConnection = this.writeConnection
    con.setAutoCommit(false)
   val res = Try{
     if(logger!=null) con.addChangeLog(logger)
     val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,str,base)
     update(str,con,u)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"UPDATE query \n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }



  def updateHandler:UpdateHandler = (query,con,upd)=> upd.execute()

  def update(query:String,logger:IChangeLog = null): Try[Unit] = this.writeUpdate(query,updateHandler,logger)




}
