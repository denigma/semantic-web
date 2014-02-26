package org.denigma.semantic.writing

import org.denigma.semantic.commons.WI
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.query.QueryLanguage
import scala.util.Try
import org.denigma.semantic.files.SemanticFileParser


/*
class that provides updates to the database
 */
trait Updater extends CanWrite  with SemanticFileParser{

  def writeUpdate(str:String,update:UpdateQuering)(implicit base:String = WI.RESOURCE) = {
    val con: BigdataSailRepositoryConnection = this.writeConnection
    con.setAutoCommit(false)
    val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,str,base)
    val res = Try{
      update(str,con,u)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"UPDATE query \n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }


  def updateHandler:UpdateQuering = (query,con,upd)=> upd.execute()

  def update(query:String): Try[Unit] = this.writeUpdate(query,updateHandler)
}
