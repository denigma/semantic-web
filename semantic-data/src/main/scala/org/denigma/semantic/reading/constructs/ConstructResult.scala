package org.denigma.semantic.reading.constructs

import scala.collection.immutable.Vector
import play.api.libs.json.{Json, JsValue}
import org.denigma.semantic.reading.QueryResultLike
import org.openrdf.query.GraphQueryResult


/*
result of CONSTRUCT SPARQL query
 */
case class ConstructResult(query:String,quads:Vector[JsValue]) extends QueryResultLike
 {

   //  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
   //  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
   lazy val asJson:JsValue = Json.obj("query"->query,"results" -> Json.obj("quads"->Json.toJson(quads)))

 }

object ConstructResult {

  def parse(query:String,results:GraphQueryResult):ConstructResult = {
    val builder =  Vector.newBuilder[JsValue]
    while(results.hasNext){
      builder.+=(Json.obj())
    }
    val v = builder.result()
    ConstructResult(query,v)
  }
}


