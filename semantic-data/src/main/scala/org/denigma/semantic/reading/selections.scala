package org.denigma.semantic.reading

import org.scalax.semweb.sesame._

import org.openrdf.query.{BindingSet, TupleQueryResult, QueryLanguage}
import com.bigdata.rdf.sail.BigdataSailTupleQuery
import scala.util._
import org.scalax.semweb.rdf.vocabulary.WI
import scala.collection.immutable._
import scala.collection.JavaConversions._
import play.api.libs.json.{JsValue, Json, JsObject}
import com.bigdata.rdf.model.{BigdataBNode, BigdataLiteral, BigdataURI}


/*
sends closures that deal with read requests
 */
trait BigDataSelectReader extends CanReadBigData with SelectReader {


  override type SelectQuery = BigdataSailTupleQuery
  override def makeSelectQuery(con: ReadConnection, query: String)(implicit base: String): SelectQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,query,base)

}


/*
can make selects
 */
trait ISelect[T] extends BigDataSelectReader{


  def select(query:String): Try[T] = this.selectQuery[T](query)(selectHandler)(WI.RESOURCE)
  protected def selectHandler:SelectHandler[T]
}

trait SimpleSelect extends ISelect[TupleQueryResult]{
  override protected def selectHandler:SelectHandler[TupleQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonSelect extends ISelect[QueryResultLike]{
  override protected def selectHandler:SelectHandler[QueryResultLike]  = (str,con,q)=>SelectResult.parse(str,q.evaluate())
}


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
