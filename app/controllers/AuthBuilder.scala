package controllers

import play.api.mvc._
import scala.concurrent.Future
import org.denigma.semantic.model.IRI
import org.denigma.semantic.vocabulary.USERS



trait UserRequestHeader extends RequestHeader{

  def username: Option[IRI]
}


object UserAction extends ActionBuilder[AuthRequest] {
  override protected def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[SimpleResult]): Future[SimpleResult] =
  {
    val user: Option[IRI] = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / name))
    val req = AuthRequest(user,request)
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
case class AuthRequest[A](username: Option[IRI], request: Request[A]) extends WrappedRequest[A](request) with UserRequestHeader
{
  def isGuest = username.isEmpty
  def isSigned = username.isDefined
}
