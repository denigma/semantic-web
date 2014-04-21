package org.denigma.semantic.reading.constructs

import scala.util.Try
import com.bigdata.rdf.sail.BigdataSailGraphQuery
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.reading.CanRead
import org.scalax.semweb.rdf.vocabulary.WI


/*
sends closure that deal with construct requests
 */
trait ConstructReader extends CanRead {

  def graphQuery[T](str:String,selectGraph:ConstructHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con= this.readConnection
    val res = Try{
      val q: BigdataSailGraphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,str,base)
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