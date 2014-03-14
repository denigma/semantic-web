package controllers

import play.api.mvc._
import org.openrdf.repository.RepositoryResult
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.model._
import play.api.libs.json.{JsValue, JsObject, Json}
import org.denigma.semantic._
import org.denigma.semantic.reading.selections.SelectResult
import org.denigma.semantic.controllers.{JsQueryController, QueryController}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import scala.util.{Try, Failure}
import scala.concurrent.Future
import org.denigma.semantic.reading.QueryResultLike
import scala.util.Failure
import play.api.libs.json.JsObject
import scala.util.Failure
import play.api.libs.json.JsObject
import play.api.mvc.SimpleResult


object  Queries extends PJaxPlatformWith("query") with JsQueryController {


  def main() = UserAction {
    implicit request=>
       Ok(pj("main",views.html.queries.main()))
  }

  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())

  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)

  def badQuery(q:String):PartialFunction[Throwable,SimpleResult] = {
    case e=>
      val er = e.getMessage
      play.Logger.info(s"Query failed \n $q \n with the following error $er")
      Ok(SelectResult.badRequest(q,er)).as("application/sparql-results+json")
  }

  def sendQuery(q:String, offset:Long = 0, limit:Long = defLimit) = UserAction.async{
    implicit request=>
      this.handleQuery(q,this.query(q,offset,limit, rewrite = false))
  }

  /*
  turns sparql query json result in Future of Simple Result
   */
  protected def handleQuery(q:String,result:Future[Try[QueryResultLike]]):Future[SimpleResult] = result.map{
    case scala.util.Success(results:QueryResultLike)=>
      Ok(results.asJson).as("application/json")
    case Failure(e)=>this.badQuery(q)(e)
  }.recover(this.badQuery(q))



}