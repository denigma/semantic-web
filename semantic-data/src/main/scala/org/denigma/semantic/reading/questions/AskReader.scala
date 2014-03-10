package org.denigma.semantic.reading.questions

import scala.util.Try
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.vocabulary.WI

trait AskReader extends CanRead
{
  /**
   * Ask
   * @param queryString query string
   * @param ask handler function that receives connection, query string and query and returns a result
   * @param base basic string (required by bigdata database)
   * @tparam T return type
   * @return result of ask function
   */
  def askQuery[T](queryString:String,ask:AskHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con = this.readConnection
    val q = con.prepareBooleanQuery(QueryLanguage.SPARQL,queryString,base)
    val res = Try{
      ask(queryString,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly ASK query\n $queryString \nfailed because of \n"+e.getMessage)
      res
    }

  }
}