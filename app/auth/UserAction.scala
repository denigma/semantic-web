package auth

import play.api.mvc._
import org.denigma.semantic.platform.AppConfig
import scala.concurrent.Future
import scala.Some
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.USERS
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.denigma.binding.play.UserAction
/**
 * User actions that contain important info about auth inside
 */
object UserAction extends ActionBuilder[AuthRequest] with AppConfig
{
  lazy val defaultDomain: Option[String] = this.currentAppConfig.getString("domain")

  override def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[Result]): Future[Result] =
  {
    val user: Option[IRI] = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / name))
    val req = AuthRequest(user,request,
      if(request.domain=="localhost" || request.domain=="192.168.122.101")
        request.session.get("domain").getOrElse(defaultDomain.getOrElse(request.domain))
      else ""
    )
    //TODO: make more safe
    block(req).map { result =>
      request.headers.get("Origin") match {
        case Some(o) => result.withHeaders("Access-Control-Allow-Origin" -> o)
        case None => result
      }
    }
  }
}
