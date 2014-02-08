package controllers.auth

import org.openrdf.model.URI
import controllers.routes
import jp.t2v.lab.play2.auth.AuthConfig
import scala.concurrent._
import play.api._
import scala.reflect.ClassTag
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import views._
import play.api.mvc._
import play.api.mvc.Results._
import jp.t2v.lab.play2.auth._
import play.api.Play._
import jp.t2v.lab.play2.stackc.{RequestWithAttributes, RequestAttributeKey, StackableController}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import reflect.{ClassTag, classTag}



/**
 * Created by antonkulaga on 2/5/14.
 */
//trait AuthConfigImpl extends AuthConfig {
//
//  /**
//   * A type that is used to identify a user.
//   * `String`, `Int`, `Long` and so on.
//   */
//  type Id = URI
//
//  /**
//   * A type that represents a user in your application.
//   * `User`, `Account` and so on.
//   */
//  type User = Account
//
//  /**
//   * A type that is defined by every action for authorization.
//   * This sample uses the following trait:
//   *
//   * sealed trait Permission
//   * case object Administrator extends Permission
//   * case object NormalUser extends Permission
//   */
//  type Authority = Permission
//
//  /**
//   * A `ClassTag` is used to retrieve an id from the Cache API.
//   * Use something like this:
//   */
//  val idTag: ClassTag[Id] = classTag[Id]
//
//  /**
//   * The session timeout in seconds
//   */
//  val sessionTimeoutInSeconds: Int = 3600
//
//  /**
//   * A function that returns a `User` object from an `Id`.
//   * You can alter the procedure to suit your application.
//   */
//  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = Account.findById(id)
//
//  /**
//   * Where to redirect the user after a successful login.
//   */
//  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
//    Future.successful(Redirect(routes.Message.main))
//
//  /**
//   * Where to redirect the user after logging out
//   */
//  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
//    Future.successful(Redirect(routes.Application.login))
//
//  /**
//   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
//   */
//  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
//    Future.successful(Redirect(routes.Application.login))
//
//  /**
//   * If authorization failed (usually incorrect password) redirect the user as follows:
//   */
//  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
//    Future.successful(Forbidden("no permission"))
//
//  /**
//   * A function that determines what `Authority` a user has.
//   * You should alter this procedure to suit your application.
//   */
//  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
//    (user.permission, authority) match {
//      case (Administrator, _)       => true
//      case (NormalUser, NormalUser) => true
//      case _                        => false
//    }
//  }
//
//  /**
//   * Whether use the secure option or not use it in the cookie.
//   * However default is false, I strongly recommend using true in a production.
//   */
//  override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)
//
//}