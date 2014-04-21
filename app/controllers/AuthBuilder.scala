package controllers

import play.api.mvc._
import scala.concurrent.Future
import org.denigma.semantic.platform.AppConfig

import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.USERS


trait UserRequestHeader extends RequestHeader{

  def username: Option[IRI]
}



object UserAction extends ActionBuilder[AuthRequest] with AppConfig
{
  lazy val defaultDomain: Option[String] = this.currentAppConfig.getString("domain")

  override protected def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[SimpleResult]): Future[SimpleResult] =
  {
    val user: Option[IRI] = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / name))
    val req = AuthRequest(user,request,if(request.domain=="localhost") defaultDomain.getOrElse(request.domain) else "")
    block(req)
  }
}

/**
 * Auth Action wrapper
 * @param action
 * @tparam A
 */
case class WithUser[A](action: Action[A]) extends Action[A] {



  def apply(request: Request[A]): Future[SimpleResult] = {
    val user = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / "name"))
    val req = AuthRequest(user,request)
    action(req)
  }

  lazy val parser = action.parser
}

/**
 * Extends request with information about user
 * @param username Username
 * @param request initial request
 * @tparam A
 */
case class AuthRequest[A](username: Option[IRI], request: Request[A], dom:String="") extends WrappedRequest[A](request) with UserRequestHeader
{
  def isGuest = username.isEmpty
  def isSigned = username.isDefined

  override lazy val domain =  if(dom=="") request.domain else dom
}
