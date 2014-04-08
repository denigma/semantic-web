package org.denigma.frontend.views

import org.scalajs.dom._
import rx.{Rx, Var}

import rx.core.Obs
import org.denigma.extensions._
import models.{RegisterPicklers=>rp}
import org.scalajs.spickling.{PicklerRegistry=>pr}
import org.scalajs.dom
import org.scalajs.spickling.{PicklerRegistry=>pr}
import scalatags.HtmlTag
import rx._

import models.{RegisterPicklers=>rp}

import org.scalajs.dom.{TextEvent, MouseEvent}

import models._

import org.scalajs.spickling.jsany._

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.jquery.{jQuery => jq}
import org.denigma.views._
import scala.collection.immutable._
import org.denigma.extensions._
import org.scalajs.dom.extensions.{AjaxException, Ajax}
import scala.util.{Failure, Success}
import org.denigma.binding._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


/**
 * Login view
 */
class LoginView(element:HTMLElement, params:Map[String,Any]) extends OrdinaryView("login",element) with Login with Registration with Signed
{
  lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvens(this)



  isSigned.handler {
    if(isSigned.now)  inRegistration() = false
  }

  val emailLogin = Rx{  login().contains("@")}

  /**
   * If anything changed
   */
  val anyChange = Rx{ (login(),password(),email(),repeat(),inLogin())}
  val clearMessage = anyChange.handler{
    message()=""
  }


}

trait Signed extends Registration {
  val onLogout = logoutClick.takeIf(this.isSigned)

  def logOut()  = Ajax.get(sq.h(s"users/logout?username=${this.login.now}&password=${this.password.now}"))

  val logoutHandler = onLogout.handler{
    this.logOut().onComplete{
      case Success(req)=>
        this.isSigned()=false
        this.clearAll()


      case Failure(ex:AjaxException)=>  this.report(s"logout failed: ${ex.xhr.responseText}")

      case _=> this.reportError("unknown failure")

    }

  }
  /**
   * Clears everything
   */
  def clearAll() = {
    this.inRegistration()=false
    this.login() = ""
    this.password()=""
    this.repeat()=""
    this.email()=""
  }
  val signupClass: Rx[String] =  Rx{
    if(this.inRegistration())
      if(this.canRegister()) "positive" else "teal"
    else
      "basic"
  }

  val loginClass: Rx[String] = Rx{
    if(this.inLogin())
      if(this.canLogin()) "positive" else "teal"
    else
      "basic"
  }
}

/**
 * Deals with login features
 */
trait Login extends BasicLogin{


  /**
   * When the user decided to switch to login
   */
  val loginToggleClick = loginClick.takeIf(inRegistration)

  /**
   * When the user comes from registration to login
   */
  val toggleLogin = this.loginToggleClick.handler{
    this.inRegistration() = false
  }

  def auth() = Ajax.get(sq.h(s"users/login?username=${this.login.now}&password=${this.password.now}"))
  val authClick = loginClick.takeIf(canLogin)
  val authHandler = authClick.handler{
    this.auth().onComplete{

      case Success(req)=>
        //dom.alert("authed successfuly")
        this.registeredName()=this.login()
        this.isSigned() = true
        //TODO: get full username
        //SessionCache.setUser(user)


      case Failure(ex:AjaxException)=>  this.report(s"Authentication failed: ${ex.xhr.responseText}")

      case _=> this.reportError("unknown failure")
    }
  }


}

/**
 * Part of the view that deals with registration
 */
trait Registration extends BasicLogin{

  /**
   * rx property binded to repeat password input
   */
  val repeat = Var("")
  val emailValid: Rx[Boolean] = Rx {email().length>4 && this.isValid(email())}

  /**
   * Email regex to check if email is valid
   * @param email
   * @return
   */
  def isValid(email: String): Boolean = """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined


  /**
   * True if password and repeatpassword match
   */
  val samePassword = Rx{
    password()==repeat()
  }
  /**
   * Reactive variable telling if register request can be send
   */
  val canRegister = Rx{ samePassword() && canLogin() && emailValid()}

  val toggleRegisterClick = this.signupClick.takeIf(this.inLogin)
  val toggleRegisterHandler = this.toggleRegisterClick.handler{
    this.inRegistration() = true
  }

  protected def register() =  Ajax.get(sq.h(s"users/register?username=${this.login.now}&password=${this.password.now}&email=${this.email.now}"))


  val registerClick = this.signupClick.takeIf(this.canRegister)
  val registerHandler = this.registerClick.handler{
    this.register().onComplete{

      case Success(req)=>
        //dom.alert("authed successfuly")
        this.registeredName()=this.login()
        this.isSigned() = true
        //TODO: get full username
        //SessionCache.setUser(user)

      case Failure(ex:AjaxException)=>  this.report(s"Registration failed: ${ex.xhr.responseText}")

      case _=> this.reportError("unknown failure")

    }
  }


}

/**
 * Basic login varibales/events
 */
trait BasicLogin extends OrdinaryView
{
  /**
   * Extracts name from global
   */
  val registeredName: Var[String] = Var(SessionCache.getUser.map(str=>if(str.contains("/")) str.substring(str.lastIndexOf("/")) else str).getOrElse("guest"))

  val login = Var("")
  val password = Var("")
  val email = Var("")
  val message = Var("")


  val isSigned = Var(registeredName()!="guest")
  val inRegistration = Var(false)
  val inLogin = Rx(!inRegistration() && !isSigned())

  val canLogin: Rx[Boolean] = Rx { login().length>4 && password().length>4 &&  password()!=login() && login()!="guest"}

  val loginClick: Var[MouseEvent] = Var(this.createMouseEvent())
  val logoutClick: Var[MouseEvent] = Var{this.createMouseEvent()}
  val signupClick: Var[MouseEvent] = Var(this.createMouseEvent())

  /**
   * Reports some info
   * @param str
   * @return
   */
  def report(str:String) = {
    this.message()=str
    str
  }

  def reportError(str:String) = dom.console.error(this.report(str))


}

//TODO: improve in future
object SessionCache {

  if(!g.session.isNullOrUndef) g.updateDynamic("session")(new js.Object())

  /**
   * Updates
   * @param user
   */
  def setUser(user:String) = this.set("user",user)

  def set(prop:String,value:String) = {
    g.session.updateDynamic(prop)(value)
  }

  def session = g \ "session"


  def getUser: Option[String] = session \ "user" map(_.toString)
  // An attempt to make FSM
  // object State {
  //    object SubState {
  //      case object Start extends SubState
  //    }
  //    class SubState
  //    def apply(value:State) = new State(SubState.Start)
  //  }
  //
  //  class State(start:State.SubState) extends Var[State.SubState](start)
  //
  //  case object Registration extends State(State.SubState.Start)
  //  {
  //    case object Writing extends State.SubState
  //    case object Submission extends State.SubState
  //    case class Rejected(reason:String) extends State.SubState
  //    case object Accepted extends State.SubState
  //
  //
  //  }
  //  case object Login extends State(State.SubState.Start){
  //
  //  }
  //
  //  case class Signed(username:String) extends State(State.SubState.Start){
  //
  //  }



}
