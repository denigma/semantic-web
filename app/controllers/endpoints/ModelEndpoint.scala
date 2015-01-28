package controllers.endpoints

import java.util.Date

import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxModelEndpoint, AuthRequest, PickleController, UserAction}
import org.denigma.semantic.controllers.{UpdateController, ShapeController}
import org.openrdf.model.Value
import org.scalax.semweb.rdf.{Res, IRI}
import org.scalax.semweb.shex.{Shape, PropertyModel}
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}
import spray.caching.{LruCache, Cache}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.util.{Success, Failure, Try}
import org.scalajs.spickling.playjson
import org.scalajs.spickling.playjson.builder

/**
 * This endpoint is for operations with property models (CRUD operations) as well as sugestions of new values
 */
trait ModelEndpoint extends AjaxModelEndpoint with PickleController
with ShapeLoader with UpdateController {
  self:Controller=>



  override type ModelRequest = AuthRequest[ModelMessage]

  override type ModelResult = Future[Result]

  def withShape(shapeId:Res,modelMessage:ModelMessage)(fun:Shape=>ModelResult) = {
    this.getShape(shapeId).flatMap{
      case Success(sh) => fun(sh)

      case Failure(th) => this.onBadModelMessage(modelMessage , "cannot find shape for creation") //BadRequest(Json.obj("status" ->"KO","message"->"cannot find shape for creation")).as("application/json")
    }
  }


  override def onCreate(createMessage: Create)(implicit request: ModelRequest): ModelResult =
  {
    this.withShape(createMessage.shapeId,createMessage){shape=>
      this.addModels(createMessage.models,Some(shape)) map {
        case Success(res) => Ok(Json.obj("satus"->"OK"))
        case Failure(th) => BadRequest( Json.obj("Status"->"Ko","message"->th.toString))
      }

    }
  }


  def modelEndpoint() = UserAction.async(this.unpickle[ModelMessage]()){implicit request=>
    this.onModelMessage(request.body)

  }



  override def onUpdate(updateMessage: Update)(implicit request: ModelRequest): ModelResult = {

    this.withShape(updateMessage.shapeId,updateMessage){shape=>
      this.addModels(updateMessage.models,Some(shape)) map {
        case Success(res) => Ok(Json.obj("satus"->"OK"))
        case Failure(th) => badResult(th.toString)
      }
    }
  }

  override def onBadModelMessage(message: ModelMessage, reason:String): ModelResult ={

    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))

  }

  /**
   * Tells about crashes
   * @param reason
   * @return
   */
  def badResult(reason:String) = BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json")


  override def onRead(readMessage: Read)(implicit request: ModelRequest): ModelResult = {
    this.withShape(readMessage.shapeId,readMessage){sh=>
        this.loadPropertyModels(readMessage.resources,sh).map{
            case Success(models) =>
              val p = rp.pickle(models)
              Ok(p).as("application/json")
            case Failure(th)=> this.badResult(th.toString)
          }
    }
  }

  override def onDelete(deleteMessage: Delete)(implicit request: ModelRequest): ModelResult = {
    ???
  }

  def suggestModels(items:List[PropertyModel], suggestMessage: Suggest): ModelResult  = {

/*    val t = suggestMessage.typed
    val list = for{
      i<-items
      p<-i.properties
      v<-p._2
      //if v.isInstanceOf[IRI] &&
      if v.stringValue.contains(t)
      if v.stringValue.length<256
    } yield v

   // val mes = ModelMessages.Suggestion(t,List[RDFValue](IRI("http://one"),IRI("http://tries"),IRI("http://something")),suggestMessage.id,suggestMessage.channel,new Date())
    val mes = Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    val p = rp.pickle(mes)
    Future.successful(Ok(p).as("application/json"))*/
    ???
  }

  override def onSuggest(suggestMessage: Suggest): ModelResult = {

/*    items.get(suggestMessage.shape)  match {
      case Some(list)=>
        suggestModels(list,suggestMessage)
      case None=> this.onBadModelMessage(suggestMessage)
    }*/

    ???
  }
}