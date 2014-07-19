package controllers.literature

import controllers.PJaxPlatformWith
import org.denigma.binding.play.UserAction
/**
 * Tools like sparql and paper viewer
 */
object Literature extends PJaxPlatformWith("literature")  with ExplorePapers with ModelPapers
{

  def reports() = UserAction{implicit request=>
    this.pj(views.html.papers.reports(request))
  }
}

//
//
//  def onMessage(message:ModelMessages.ModelMessage)(implicit request:RequestType): Future[Result] = message match {
////    case m:ModelMessages.Create=>this.onCreate(m)
////    case m:ModelMessages.Read=>this.onRead(m)
////    case m:ModelMessages.Update=>this.onUpdate(m)
////    case m:ModelMessages.Delete=>this.onDelete(m)
//    case m:ModelMessages.SelectQuery=>this.onSelect(m)
//
//    case _=> Future.successful(this.BadRequest("unsupported message format"))
//  }
