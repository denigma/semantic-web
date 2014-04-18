package org.denigma.semantic.reading

import scala.util.Try
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

/*
trait that does read operations
 */
trait DataReader extends CanRead {


  def read[T](action:Reading[T]):Try[T]= {
    val con: BigdataSailRepositoryConnection = this.readConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      lg.error("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }


}
