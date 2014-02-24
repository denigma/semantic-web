package org.denigma.semantic.writing

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.util.Try
import org.openrdf.query.QueryLanguage
import scala.util.Failure
import scala.util.Success
import org.denigma.semantic.commons.{WI, Logged}


/*
interface for data writing
 */
trait DataWriter extends Logged{


  def writeConnection: BigdataSailRepositoryConnection


  /*
 writes something and then closes the connection
  */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = this.writeConnection
    con.setAutoCommit(false)
    val res = Try {
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

  /*
writes something and then closes the connection
 */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean = this.readWrite[Unit](action) match
  {
    case Success(_)=>true
    case Failure(e)=>false
  }

  def update[T](str:String,update:UpdateQuering[T])(implicit base:String = WI.RESOURCE) = {
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

}
