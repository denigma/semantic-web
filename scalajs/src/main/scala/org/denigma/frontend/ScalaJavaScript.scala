package org.denigma.frontend

import org.scalajs.dom
import org.scalajs.spickling.{PicklerRegistry=>pr}
import scala.scalajs.js
import scalatags.all._
import scalatags.HtmlTag
import rx._
import scala.scalajs.js.annotation.JSExport

import models.{RegisterPicklers=>rp}

import org.scalajs.dom.{TextEvent, MouseEvent, console}

import models._

import org.scalajs.spickling.jsany._
import scala.collection.immutable._

import js.Dynamic.{ global => g }
import org.scalajs.jquery.{jQuery => jq, JQueryXHR}
import org.denigma.frontend.views._
import org.denigma.views._
import scala.Predef

@JSExport
object ScalaJavaScript extends OrdinaryView("main",dom.document.body) {


  org.denigma.views
    .register("login", (el, params) => new LoginView(el))
    .register("menu", (el, params) => new MenuView(el))
    .register("random",(el,params)=> new RandomView(el))

  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

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


  @JSExport
  def main(): Unit = {

    rp.registerPicklers()

    val str = s"http://${dom.window.location.host}/test"

    this.bind(this.element)

  }

  override def textEvents: Predef.Map[String, Var[TextEvent]] = this.extractTextEvents(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)
}