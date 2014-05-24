package auth

import org.scalax.semweb.rdf.{Res, IRI}
import play.api.mvc.{WrappedRequest, Request}
import org.scalax.semweb.rdf.vocabulary.USERS
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.UserRequestHeader

/**
 * Extends request with information about user
 * @param username Username
 * @param request initial request
 * @tparam A
 */
case class AuthRequest[A](username: Option[IRI], request: Request[A], dom:String="") extends WrappedRequest[A](request) with UserRequestHeader
{
  def isGuest = username.isEmpty
  override def isSigned = username.isDefined

  def currentUser: IRI = if(isGuest) IRI( USERS / "Guest" ) else username.get


  def pjax: Option[String] = request.headers.get("X-PJAX")

  override lazy val domain =  if(dom=="") request.domain else dom

  //isSigned && this.policies.exists(p=>p.canCreate(username.get,res))
//  override def canCreate(res: Res): Boolean = policy.canCreate(currentUser,res)
//
//  override def canRead(res: Res): Boolean = policy.canRead(currentUser,res)
//
//  override def canUpdate(res: Res): Boolean = policy.canUpdate(currentUser,res)
//
//  override def canDelete(res: Res): Boolean = policy.canDelete(currentUser,res)

}

