package org.denigma.semantic.reading

import play.api.libs.json.Json
import org.scalax.semweb.sesame.AskReader
import com.bigdata.rdf.sail.BigdataSailBooleanQuery
import org.openrdf.query.QueryLanguage
import scala.util.Try
import org.scalax.semweb.rdf.vocabulary.WI

/*
Json result of ask query
 */
class AskResult(str:String, val bool:Boolean) extends SelectResult(str,List.empty,List.empty)
{

  override lazy val asJson = Json.obj("query"->query,"boolean"->bool)
}

object AskResult{
  def unapply(a: AskResult): Option[(String, Boolean)] = Some(a.query -> a.bool)
  def apply(query:String,bool:Boolean) = new AskResult(query,bool)
}


trait BigDataAskReader extends CanReadBigData with AskReader{
  type AskQuery = BigdataSailBooleanQuery

  override def makeAskQuery(con: ReadConnection, query: String)(implicit base: String): AskQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL,query,base)
}

trait IAsk[T] extends BigDataAskReader{
  protected def askHandler:AskHandler[T]
  def question(query:String): Try[T] = this.askQuery(query)(askHandler)(base = WI.RESOURCE)
}

trait SimpleAsk extends IAsk[Boolean] {
  override protected def askHandler:AskHandler[Boolean]  = (str,con,q)=>q.evaluate()
}

trait JsonAsk extends IAsk[QueryResultLike] {
  override protected def askHandler:AskHandler[QueryResultLike]  = (str,con,q)=>AskResult(str,q.evaluate())
}