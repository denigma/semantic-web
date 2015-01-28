package controllers.endpoints

import java.util.Date
import scala.collection.JavaConversions._
import controllers.{WithQuerySearch, PJaxPlatformWith}
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages.ExploreMessage
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxExploreEndpoint, AuthRequest, PickleController, UserAction}
import org.denigma.semantic.controllers.{ShapeController, SimpleQueryController}
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.{Shape, PropertyModel}
import play.api.libs.json.Json
import play.api.mvc.Result
import org.scalajs.spickling.playjson._
import play.twirl.api.Html
import spray.caching.{LruCache, Cache}

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}
import org.openrdf.model.Value
import org.scalax.semweb.sesame._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Explore articles trait
 */
trait ExploreEndpoint extends PickleController
  with AjaxExploreEndpoint with ShapeController with WithQuerySearch
{
  self:PJaxPlatformWith =>



  override type ExploreRequest = AuthRequest[ExploreMessage]

  override type ExploreResult = Future[Result]

  def exploreEndpoint() = UserAction.async(this.unpickle[ExploreMessage]()){implicit request=>
    this.onExploreMessage(request.body)

  }

  protected def shapeQueryModels(shape:Res,query:Res): Future[Try[(Shape, Set[PropertyModel])]] = this.queryFor(query).flatMap{   case q=>   this.stringSelectWithShapeRes(q,shape)    }


  override def onExplore(exploreMessage: ExploreMessages.Explore)(implicit request: ExploreRequest): ExploreResult = {

    val propShape: Future[Try[(Shape, Set[PropertyModel])]] = this.shapeQueryModels(exploreMessage.shape,exploreMessage.query)
    propShape.map{
      case Success((shape,list))=>Ok(rp.pickle( ExploreMessages.Exploration(shape,list.toList,exploreMessage) )).as("application/json")

      case Failure(th)=>this.tellBad(th.getMessage)
    }
  }


  protected def suggest(suggestMessage: ExploreMessages.ExploreSuggest, items:List[PropertyModel])(implicit request: ExploreRequest): ExploreResult = {

    val t = suggestMessage.typed
    val nc = suggestMessage.nameClass
    val exp = suggestMessage.explore
    val q = exp.query


/*
    //play.Logger.debug("original = "+suggestMessage.toString)
    val t = suggestMessage.typed
    val prop = suggestMessage.prop
    val list = exploreItems(items,suggestMessage.explore)
    //play.Logger.debug("basic list = "+suggestMessage.toString)

    val result = list
      .collect { case item if item.properties.contains(prop) =>
      item.properties(prop).collect {
        case p if p.stringValue.contains(t) => p
      }
    }.flatten

      val mes = ExploreMessages.ExploreSuggestion(t, result, suggestMessage.id, suggestMessage.channel, new Date())
      val p = rp.pickle(mes)
      Future.successful(Ok(p).as("application/json"))
*/
    ???
  }



  override def onExploreSuggest(suggestMessage: ExploreMessages.ExploreSuggest)(implicit request: ExploreRequest): ExploreResult = {
/*
    this.items.get( suggestMessage.explore.shape)  match
    {
      case Some(list)=>this.suggest(suggestMessage, list)(request)
      case None=> this.onBadExploreMessage(suggestMessage)
    }*/
    ???
  }

  override def onBadExploreMessage(message: ExploreMessages.ExploreMessage, reason:String)(implicit request: ExploreRequest): ExploreResult ={

    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))

  }

  override def onSelect(suggestMessage: ExploreMessages.SelectQuery)(implicit request: ExploreRequest): ExploreResult = {
 /*   this.items.get( suggestMessage.shapeId)  match {

      case Some(list)=>
        Future.successful(Ok(rp.pickle(list)).as("application/json"))

      case None=> this.onBadExploreMessage(suggestMessage,"cannot find shape for the message")
    }*/
  ???
  }

  override def onBadExploreMessage(message: ExploreMessage)(implicit request: ExploreRequest): ExploreResult = Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))


}
