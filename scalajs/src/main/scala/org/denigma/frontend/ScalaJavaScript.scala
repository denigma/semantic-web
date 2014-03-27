package org.denigma.frontend

import org.scalajs.dom
import org.scalajs.spickling.{PicklerRegistry=>pr}
import scala.scalajs.js
import scalatags.all._
import scalatags.{UntypedAttr, HtmlTag}
import rx._
import extensions._
import scala.scalajs.js.annotation.JSExport

import models.{RegisterPicklers=>rp}

import org.scalajs.dom.{HTMLElement, console}

import models._

import org.scalajs.spickling.jsany._
import scala.collection.immutable._
import scala.util.Random

import js.Dynamic.{ global => g }
import org.scalajs.jquery.{jQuery => jq, JQueryXHR}
import dom.extensions._
import org.denigma.frontend.views.{OrdinaryView, MenuView}
import scala.collection.mutable

@JSExport
object ScalaJavaScript extends OrdinaryView("main",dom.document.body) {


  val isSigned: Var[Boolean] = Var(false)


  views
    .register("menu", (el, params) => new MenuView(el))
    .register("random",(el,params)=>new RandomView(el))

  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  def onSuccess(data: js.Any, textStatus: js.String, jqXHR: JQueryXHR) = {
    val d = pr.unpickle(data)
    val m = d.asInstanceOf[Message]
    console.log(s"data=$data,text=$textStatus,jqXHR=$jqXHR")
  }


  def send(path: String, message: Message) = {
    val mes = pr.pickle(message)
    val settings = js.Dynamic.literal(
      url = path,
      success = {
        this.onSuccess _
      },
      error = {
        (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
          console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
      },
      contentType = "application/json",
      dataType = "json",
      data = g.JSON.stringify(mes),
      `type` = "POST"
    ).asInstanceOf[org.scalajs.jquery.JQueryAjaxSettings]
  }


  @JSExport
  def main(): Unit = {

    rp.registerPicklers()

    val str = s"http://${dom.window.location.host}/test"

    this.bind(this.element)



  }

}