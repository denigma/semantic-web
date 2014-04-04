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

//import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Login view
 */
class LoginView(element:HTMLElement, params:Map[String,Any]) extends OrdinaryView("login",element)
{
  lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvens(this)


  val loginClick: Var[MouseEvent] = Var(this.createMouseEvent())

  val registerClick: Var[MouseEvent] = Var(this.createMouseEvent())

  val isSigned: Var[Boolean] = Var(false)

  val inSigningUp = Var(false)

  val inLogging:Rx[Boolean] = Rx { !this.inSigningUp() && !isSigned()}
  val login = Var("")
  val password = Var("")
  val repeat = Var("")

  val onSigningUp = Obs(isSigned) {
    if(this.inSigningUp.now) this.inSigningUp()= false else{

    }
  }


  val canSend = Rx{
    val p = password()
    val r = repeat()
    ( p==r || !inSigningUp() ) && p.length>4 && login().length>0
  }

  val onLoginClick = loginClick.handler{

    this.sendTest()

    if(this.inSigningUp.now){
      this.inSigningUp() = false
    }
    else
    {
      if(canSend()){

      }
    }
  }
  val emailLogin = Rx{  login().contains("@")}



  val onSignUpClick = Obs(this.registerClick){
    if(this.inSigningUp.now){

    } else {
      this.inSigningUp()=true
    }
  }


  def sendTest() = {
    rp.registerPicklers()
    val m: Message = Message(User("someUser"),"hello")
    val data = pr.pickle(m)
    val mm = pr.unpickle(data)
    dom.alert("test pickling: "+m.toString)
    /*
    sq.post(sq.h("test"),data).onComplete{
      case Success(res:XMLHttpRequest)=>
        dom.alert("post works! "+res.response.toString)
      case Failure(er)=>
        dom.alert(s"post does not work and throws ${er.toString} error!")
    }
    */
  }




}

//  def onSuccess(data: js.Any, textStatus: js.String, jqXHR: JQueryXHR) = {
//    val d = pr.unpickle(data)
//    val m = d.asInstanceOf[Message]
//    console.log(s"data=$data,text=$textStatus,jqXHR=$jqXHR")
//  }
//
//
//  def send(path: String, message: Message) = {
//    val mes = pr.pickle(message)
//    val settings = js.Dynamic.literal(
//      url = path,
//      success = {
//        this.onSuccess _
//      },
//      error = {
//        (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
//          console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
//      },
//      contentType = "application/json",
//      dataType = "json",
//      data = g.JSON.stringify(mes),
//      `type` = "POST"
//    ).asInstanceOf[org.scalajs.jquery.JQueryAjaxSettings]
//  }
