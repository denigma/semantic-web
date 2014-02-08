package controllers.auth

import play.api._
import jp.t2v.lab.play2.auth.{AuthElement, LoginLogout}
import play.api.data._
import play.api.mvc._
import scala.concurrent.Future
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





//import org.mindrot.jbcrypt.BCrypt
//import scalikejdbc._, SQLInterpolation._
//
//case class Account(id: Int, email: String, password: String, name: String, permission: Permission)

//object Account extends SQLSyntaxSupport[Account] {
//
//  val a = syntax("a")
//
//  def apply(a: SyntaxProvider[Account])(rs: WrappedResultSet): Account = apply(a.resultName)(rs)
//  def apply(a: ResultName[Account])(rs: WrappedResultSet): Account = new Account(
//    id         = rs.int(a.id),
//    email      = rs.string(a.email),
//    password   = rs.string(a.password),
//    name       = rs.string(a.name),
//    permission = Permission.valueOf(rs.string(a.permission))
//  )
//
//  private val auto = AutoSession
//
//  def authenticate(email: String, password: String)(implicit s: DBSession = auto): Option[Account] = {
//    findByEmail(email).filter { account => BCrypt.checkpw(password, account.password) }
//  }
//
//  def findByEmail(email: String)(implicit s: DBSession = auto): Option[Account] = withSQL {
//    select.from(Account as a).where.eq(a.email, email)
//  }.map(Account(a)).single.apply()
//
//  def findById(id: Int)(implicit s: DBSession = auto): Option[Account] = withSQL {
//    select.from(Account as a).where.eq(a.id, id)
//  }.map(Account(a)).single.apply()
//
//  def findAll()(implicit s: DBSession = auto): Seq[Account] = withSQL {
//    select.from(Account as a)
//  }.map(Account(a)).list.apply()
//
//  def create(account: Account)(implicit s: DBSession = auto) {
//    withSQL {
//      import account._
//      val pass = BCrypt.hashpw(account.password, BCrypt.gensalt())
//      insert.into(Account).values(id, email, pass, name, permission.toString)
//    }.update.apply()
//  }
//
//}

//
//sealed trait Permission
//case object Administrator extends Permission
//case object NormalUser extends Permission
//
//object Permission {
//
//  def valueOf(value: String): Permission = value match {
//    case "Administrator" => Administrator
//    case "NormalUser"    => NormalUser
//    case _ => throw new IllegalArgumentException()
//  }
//
//}

//
//trait AuthConfigImpl extends AuthConfig {
//
//  type Id = Int
//
//  type User = Account
//
//  type Authority = Permission
//
//  val idTag: ClassTag[Id] = classTag[Id]
//
//  val sessionTimeoutInSeconds = 3600
//
//  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful(Account.findById(id))
//
//  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Messages.main))
//
//  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.login))
//
//  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.login))
//
//  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Forbidden("no permission"))
//
//  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext) = Future.successful((user.permission, authority) match {
//    case (Administrator, _) => true
//    case (NormalUser, NormalUser) => true
//    case _ => false
//  })
//
//  //  override lazy val idContainer = new CookieIdContainer[Id]
//
//}
//
//trait Pjax extends StackableController {
//  self: Controller with AuthElement with AuthConfigImpl =>
//
//  type Template = String => Html => Html
//
//  case object TemplateKey extends RequestAttributeKey[Template]
//
//  abstract override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[SimpleResult]): Future[SimpleResult] = {
//    val template: Template = if (req.headers.keys("X-Pjax")) html.pjaxTemplate.apply else html.fullTemplate.apply(loggedIn(req))
//    super.proceed(req.set(TemplateKey, template))(f)
//  }
//
//  implicit def template[A](implicit req: RequestWithAttributes[A]): Template = req.get(TemplateKey).get
//
//}