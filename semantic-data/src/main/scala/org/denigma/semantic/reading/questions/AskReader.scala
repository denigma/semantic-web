package org.denigma.semantic.reading.questions

import scala.util.Try
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.commons.WI

trait AskReader extends CanRead
{

  /*
  ASK query
  */
  def askQuery[T](str:String,ask:AskQuerying[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con = this.readConnection
    val q = con.prepareBooleanQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      ask(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly ASK query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }
}