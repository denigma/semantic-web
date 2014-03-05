package org.denigma.semantic.reading.selections

import com.bigdata.rdf.sail.{BigdataSailTupleQuery, BigdataSailRepositoryConnection}
import org.openrdf.query.QueryLanguage
import scala.util.Try
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.commons.WI

/*
sends closures that deal with read requests
 */
trait SelectReader extends CanRead
{

  /*
  readonly select query failed
   */
  def selectQuery[T](str:String,select:SelectHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con: BigdataSailRepositoryConnection = this.readConnection
    val res = Try{
      val q:BigdataSailTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,str,base)
      select(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly SELECT query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }
}