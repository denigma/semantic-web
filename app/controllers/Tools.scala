package controllers

import auth.UserAction
import org.denigma.binding.picklers.rp
import org.denigma.semantic.controllers._
import org.denigma.semantic.reading.{QueryResultLike, SelectResult}
import org.openrdf.query.TupleQueryResult
import org.scalajs.spickling.playjson._
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.StringQueryMessages
import org.scalax.semweb.picklers.SemanticRegistry
import org.scalax.semweb.rdf._
import org.scalax.semweb.sesame._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result

import scala.collection.JavaConversions._
import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Tools like sparql and paper viewer
 */
object Tools extends PJaxPlatformWith("tools") with UpdateController with SimpleQueryController with PickleController {

  def sparqlEndpoint = UserAction.async(this.pickle[StringQueryMessages.Select]()(rp)) {
    implicit request=>
      val q= request.body.query
      play.Logger.debug("incoming="+q)

       this.select(q) map {
         case Success(res: TupleQueryResult)=>
           val mp = res.toListMap
           play.Logger.debug("mp = "+mp.toString)

           val results:List[Map[String,RDFValue]] = mp.map(rv=>rv.map(kv=>(kv._1,kv._2:RDFValue)).toMap)
           val selectResults: SelectResults = SelectResults(res.getBindingNames.toList,  results)
           val p = SemanticRegistry.pickle(      selectResults       )
             Ok(p).as("application/json")
         case Failure(th)=>
           play.Logger.error(s"error in sparql query $q \n"+th.getMessage.toString)
           this.badQuery(q)(th)

       }

  }

//  def testSelect = UserAction.async {
//    implicit request=>
//      val q = "SELECT  ?subject ?property ?object WHERE  { ?subject ?property ?object }"
//
//      this.select(q) map {
//        case Success(res: TupleQueryResult)=>
//          val results:List[Map[String,RDFValue]] = res.toListMap.map(rv=>rv.map(kv=>(kv._1,kv._2:RDFValue)).toMap)
//          val selectResults: SelectResults = SelectResults(res.getBindingNames.toList,  results)
//          val p = SemanticRegistry.pickle(      selectResults       )
//          Ok(p).as("application/json")
//        case Failure(th)=>
//          play.Logger.error(s"error in sparql query $q \n"+th.getMessage.toString)
//          this.badQuery(q)(th)
//
//      }
//
//  }


  def sparql = UserAction {
    implicit request=>
      val pdf = views.html.tools.query(request)
      this.pj(pdf)(request)
  }

  def paper = UserAction {
    implicit request=>
      val pdf = views.html.tools.paper(request)
      this.pj(pdf)(request)
  }

  def users = UserAction{
    implicit request=>
      val users = views.html.tools.users(request)
      this.pj(users)(request)
  }

  //  def main() = UserAction {
  //    implicit request=>
  //       Ok(pj("main",views.html.queries.main()))
  //  }

  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())

  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)

  def badQuery(q:String):PartialFunction[Throwable,Result] = {
    case e=>
      val er = e.getMessage
      play.Logger.info(s"Query failed \n $q \n with the following error $er")
      Ok(SelectResult.badRequest(q,er)).as("application/sparql-results+json")
  }

//  def sendQuery(q:String, offset:Long = 0, limit:Long = defLimit) = UserAction.async{
//    implicit request=>
//      this.handleQuery(q,this.query(q,offset,limit, rewrite = false))
//  }

  /*
  turns sparql query json result in Future of Simple Result
   */
  protected def handleQuery(q:String,result:Future[Try[QueryResultLike]]):Future[Result] = result.map{
    case scala.util.Success(results:QueryResultLike)=>
      Ok(results.asJson).as("application/json")
    case Failure(e)=>this.badQuery(q)(e)
  }.recover(this.badQuery(q))

}
