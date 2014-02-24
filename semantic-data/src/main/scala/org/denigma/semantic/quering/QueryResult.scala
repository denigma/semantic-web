package org.denigma.semantic.quering

import play.api.libs.json.{JsValue, Json, JsObject}
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.query.{GraphQueryResult, TupleQueryResult, BindingSet}
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

//  def parse(results:TupleQueryResult) = {
//    val vars: List[String] = results.getBindingNames.toList
//    var re= List.empty[BindingSet]
//    while(results.hasNext){
//      val el= results.next()
//      re = el::re
//    }
//    re
//  }

  def parse(query:String,results:GraphQueryResult):ConstructResult = {
    val builder =  Vector.newBuilder[JsValue]
    while(results.hasNext){
      builder.+=(Json.obj())
    }
    val v = builder.result()
    ConstructResult(query,v)
  }

  def parse(query:String,results:Boolean):AskResult = AskResult(query,results)



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

object AskResult{
  def unapply(a: AskResult): Option[(String, Boolean)] = Some(a.query -> a.bool)
  def apply(query:String,bool:Boolean) = new AskResult(query,bool)
}
class AskResult(str:String, val bool:Boolean) extends QueryResult(str,List.empty,List.empty)
{

  override lazy val asJson = Json.obj("query"->query,"boolean"->bool)
}

case class QueryResult(query:String,vars:scala.Seq[String],bindings:List[Map[String,JsObject]]) extends QueryResultLike
{

//  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
//  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
  lazy val asJson:JsValue = Json.obj("query"->query,"head"->Json.obj("vars"->vars),"results" -> Json.obj("bindings"->bindings ))

}

case class ConstructResult(query:String,quads:Vector[JsValue]) extends QueryResultLike
{

  //  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
  //  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
  lazy val asJson:JsValue = Json.obj("query"->query,"results" -> Json.obj("quads"->Json.toJson(quads)))

}

trait QueryResultLike{
  val query:String
  val asJson:JsValue
}