package org.denigma.semantic.reading.selections

import play.api.libs.json.{JsValue, Json, JsObject}
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.query.{GraphQueryResult, TupleQueryResult, BindingSet}
import com.bigdata.rdf.model._
import org.denigma.semantic.reading.constructs.ConstructResult
import org.denigma.semantic.reading.QueryResultLike
import org.denigma.semantic.reading._


object SelectResult {

  /**
  TODO:rewrite
   */
  def parse(query:String,results:TupleQueryResult):SelectResult = {
    val vars: List[String] = results.getBindingNames.toList
    var re= List.empty[Map[String,JsObject]]
    while(results.hasNext){
      val el: Map[String,JsObject] = binding2List(vars,results.next())
      re = el::re
    }
    SelectResult(query,vars,re.reverse)
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




case class SelectResult(query:String,vars:scala.Seq[String],bindings:List[Map[String,JsObject]]) extends QueryResultLike
{

//  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
//  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
  lazy val asJson:JsValue = Json.obj("query"->query,"head"->Json.obj("vars"->vars),"results" -> Json.obj("bindings"->bindings ))

}
