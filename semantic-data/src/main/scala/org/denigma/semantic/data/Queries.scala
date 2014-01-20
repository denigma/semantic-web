package org.denigma.semantic.data

import play.api.libs.json.{JsValue, Json, JsObject}
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.query.{TupleQueryResult, BindingSet}
import com.bigdata.rdf.model._

object QueryResult {
  def parse(query:String,results:TupleQueryResult):QueryResult = {
    val vars: List[String] = results.getBindingNames.toList
    var re= List.empty[Map[String,JsObject]]
    while(results.hasNext){
      val el: Map[String,JsObject] = binding2List(vars,results.next())
      re = el::re
    }
    QueryResult(query,vars,re.reverse)
  }

  def badRequest(query:String,error:String): JsObject =
    Json.obj("query"->query,"errors"->Json.arr(error),"head"->Json.obj("vars"->Json.arr()),"results" -> Json.obj("bindings"->Json.arr() ))


  def binding2List(names:Seq[String],b:BindingSet): Map[String,JsObject] =
    names.map{
      case name=>
        b.getValue(name) match {
          case uri:BigdataURI=>name->Json.obj("type"-> "uri" , "value" -> uri.stringValue())
          case literal:BigdataLiteral=>name->Json.obj("type"-> "literal" , "value" -> literal.stringValue())
          case bnode:BigdataBNode=>name->Json.obj("type"-> "literal" , "value" -> bnode.stringValue())
          case el:Any=>
            play.Logger.error(s"unknown rdf type during serialization $el")
            name->Json.obj("type"-> "unknown" , "value" -> el.stringValue())
        }
    }.toMap

}

case class QueryResult(query:String,vars:scala.Seq[String],bindings:List[Map[String,JsObject]])
{

//  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
//  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
  lazy val asJson = Json.obj("query"->query,"head"->Json.obj("vars"->vars),"results" -> Json.obj("bindings"->bindings ))

}