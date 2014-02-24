package org.denigma.semantic.reading.questions

import org.denigma.semantic.reading.selections.SelectResult
import play.api.libs.json.Json

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