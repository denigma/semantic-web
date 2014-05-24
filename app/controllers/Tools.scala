package controllers

import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.controllers._
import play.api.libs.json.{JsValue, Json, JsObject}
import scala.collection.immutable.Map
import play.api.mvc.SimpleResult
import scala.concurrent.Future
import scala.util.{Failure, Try}
import org.denigma.semantic.reading.{SelectResult, QueryResultLike}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import auth.UserAction

/**
 * Tools like sparql and paper viewer
 */
object Tools extends PJaxPlatformWith("tools") with UpdateController with JsQueryController {

  def sparql = UserAction {
    implicit request=>
      val pdf = views.html.query(request)
      this.pj(pdf)(request)
  }

  def paper = UserAction {
    implicit request=>
      val pdf = views.html.paper(request)
      this.pj(pdf)(request)
  }

  //  def main() = UserAction {
  //    implicit request=>
  //       Ok(pj("main",views.html.queries.main()))
  //  }

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
