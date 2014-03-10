package org.denigma.semantic.reading.queries

import scala.util.Try
import com.bigdata.rdf.sail.{BigdataSailQuery, BigdataSailRepositoryConnection}
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.reading._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.constructs._
import org.denigma.semantic.reading.questions._
import org.denigma.semantic.vocabulary.WI


/**
sends closures that deal with all requests
 */
trait AnyQueryReader extends CanRead {


  def anyQuery[T](str:String,select:AnyQueryHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con: BigdataSailRepositoryConnection = this.readConnection
    val res = Try{
      val q: BigdataSailQuery = con.prepareNativeSPARQLQuery(QueryLanguage.SPARQL,str,base)
      select(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"READONLY any QUERY \n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

}