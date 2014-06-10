package auth

import org.denigma.binding.play.UserRequestHeader
import org.scalax.semweb.rdf.{Res, IRI}
import play.api.mvc.{WrappedRequest, Request}
import org.scalax.semweb.rdf.vocabulary.USERS
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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

  def currentUser: IRI = if(isGuest) IRI( USERS / "Guest" ) else username.get


  def pjax: Option[String] = request.headers.get("X-PJAX")

  override lazy val domain =  if(dom=="") request.domain else dom


}

