package controllers.literature
/*

import auth.{UserAction, AuthRequest}
import controllers.{PJaxPlatformWith, PickleController}
import org.denigma.binding.messages.ExploreMessages._
import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.play.{ AjaxModelEndpoint}
import org.denigma.semantic.controllers.ShapeController
import play.api.libs.json.Json
import play.api.mvc.Result

import scala.concurrent.Future


trait ModelPapers extends PickleController with ShapeController with ArticleShapes with AjaxModelEndpoint{
  self:PJaxPlatformWith=>

  override type ModelRequest = AuthRequest[ExploreMessage]

  override type ModelResult = Future[Result]

  override def onUpdate(updateMessage: Update)(implicit request: ModelRequest): ModelResult = {
    ???
  }

  override def onBadModelMessage(message: ModelMessage): ModelResult = {
    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"bad explore message")).as("application/json"))
  }

  override def onSuggest(suggestMessage: Suggest): ModelResult = {
    ???
  }

  override def onRead(readMessage: Read)(implicit request: ModelRequest): ModelResult = {

    ???
  }

  override def onDelete(deleteMessage: Delete)(implicit request: ModelRequest): ModelResult = {
    ???
  }

  override def onCreate(createMessage: Create)(implicit request: ModelRequest): ModelResult ={
    ???
  }


  def modelEndpoint() = UserAction.async(this.pickle[ExploreMessage]()){implicit request=>

    this.onModelMessage(request.body)

  }
}
*/
