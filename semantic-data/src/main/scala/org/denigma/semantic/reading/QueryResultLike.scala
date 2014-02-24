package org.denigma.semantic.reading

import play.api.libs.json.JsValue


/*
query result as JsValue
 */
trait QueryResultLike{
  val query:String
  val asJson:JsValue
}