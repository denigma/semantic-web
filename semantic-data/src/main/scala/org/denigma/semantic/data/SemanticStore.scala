package org.denigma.semantic.data

import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailRepository}
import scala.util.{Failure, Success, Try}

/**
 * Created by antonkulaga on 1/20/14.
 */
abstract class SemanticStore(implicit lg:org.slf4j.Logger) {

  val repo: BigdataSailRepository

  def read[T](action:BigdataSailRepositoryConnection=>T):Try[T]= {
    val con = repo.getReadOnlyConnection
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

  /*
  writes something and then closes the connection
   */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = repo.getConnection
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
 Shutdown repository
  */
  def close()={
    repo.shutDown()
  }

  /*
does something with Sesame connection and then closes it
 */
  def withConnection(con:BigdataSailRepositoryConnection)(action:BigdataSailRepositoryConnection=>Unit) = {
    if(!con.isReadOnly) con.setAutoCommit(false)
    Try {
      action(con)
      if(!con.isReadOnly) con.commit()
    }.recover{case f=>lg.error(f.toString)}
    lg.debug("operation successful")
    con.close()
  }


  /*
  writes something and then closes the connection
   */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean = this.readWrite[Unit](action) match
  {
    case Success(_)=>true
    case Failure(e)=>false
  }



}
