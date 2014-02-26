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
trait DataWriter extends CanWrite{


  def writeConnection: BigdataSailRepositoryConnection


  /*
 writes something and then closes the connection
  */
  def write[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
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


}
