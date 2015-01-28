package controllers.literature

import controllers.PJaxPlatformWith
import org.denigma.binding.messages.ExploreMessages.ExploreMessage
import org.denigma.binding.messages.ModelMessages.ModelMessage
import org.denigma.binding.play.UserAction
import play.api.libs.json.Json

import scala.concurrent.Future

/**
 * Tools like sparql and paper viewer
 */
object Literature extends PJaxPlatformWith("literature")//  with ExplorePapers with ModelPapers
{

  def reports() = UserAction{implicit request=>
    this.pj(views.html.papers.reports(request))
  }

  /*override def onBadExploreMessage(message: ExploreMessage, reason: String)(implicit request: Literature.ExploreRequest): Literature.ExploreResult = {
   Future.successful( BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json") )
  }

  override def onBadModelMessage(message: ModelMessage, reason:String): ModelResult = {
    Future.successful( BadRequest(Json.obj("status" -> "KO", "message" -> reason)).as("application/json") )
  }
*/
}

