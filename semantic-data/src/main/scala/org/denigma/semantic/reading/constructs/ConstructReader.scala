package org.denigma.semantic.reading.constructs

import scala.util.Try
import com.bigdata.rdf.sail.BigdataSailGraphQuery
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.commons.WI

/*
sends closure that deal with construct requests
 */
trait ConstructReader extends CanRead {

  def graphQuery[T](str:String,selectGraph:ConstructQuerying[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con= this.readConnection
    val q: BigdataSailGraphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      selectGraph(str,con,q)

    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly GRAPH query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }
}