package org.denigma.semantic.data

case class QueryResult(query:String,names:scala.List[String],rows: List[Map[String, String]])
