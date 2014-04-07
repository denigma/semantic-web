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
import org.scalajs.dom.extensions.Ajax
import scala.util.{Failure, Success}
import org.denigma.binding._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


/**
 * Login view
 */
class LoginView(element:HTMLElement, params:Map[String,Any]) extends OrdinaryView("login",element) with Login with Registration
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
      case Failure(f)=>dom.alert(s"auth failure: ${f.toString}")
      case Success(req)=>
        dom.alert("authed successfuly")
        this.isSigned() = true
    }
  }


}

/**
 * Deals with registration
 */
trait Registration extends BasicLogin{

 val repeat = Var("")

  val samePassword = Rx{
    password()==repeat()
  }
  val canRegister = Rx{ samePassword() && canLogin() && emailValid()}

  val toggleRegisterClick = this.signupClick.takeIf(this.inLogin)
  val toggleRegisterHandler = this.toggleRegisterClick.handler{
    this.inRegistration() = true
  }

  protected def register() =  Ajax.get(sq.h(s"users/register?username=${this.login.now}&password=${this.password.now}&email=${this.email.now}"))


  val registerClick = this.signupClick.takeIf(this.canRegister)
  val registerHandler = this.registerClick.handler{
    this.register().onComplete{
      case Failure(f)=>dom.alert(s"registration failure: ${f.toString}")
      case Success(req)=>
        dom.alert("registered successfuly")
        this.isSigned() = true
    }
  }

  val emailValid: Rx[Boolean] = Rx {email().length>4 && this.isValid(email())}

  def isValid(email: String): Boolean = """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined



}

/**
 * Basic login variables
 */
trait BasicLogin extends OrdinaryView
{



  val login = Var("")
  val password = Var("")
  val email = Var("")


  val isSigned = Var(false)
  val inRegistration = Var(false)
  val inLogin = Rx(!inRegistration())

  val canLogin: Rx[Boolean] = Rx { login().length>4 && password().length>4 &&  password()!=login() }

  val loginClick: Var[MouseEvent] = Var(this.createMouseEvent())

  val signupClick: Var[MouseEvent] = Var(this.createMouseEvent())
}



object LoginView {
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
