package org.denigma.frontend

import org.scalajs.dom
import scalatags.all._
import scalatags.HtmlTag
import rx._
import scala.scalajs.js.annotation.JSExport
import org.denigma.extensions._
import models.{RegisterPicklers=>rp}

import org.scalajs.dom.{MouseEvent, console}

import models._


import scalajs.js.Dynamic.{ global => g }
import org.scalajs.jquery.{jQuery => jq, JQueryXHR}
import org.denigma.frontend.views._
import org.denigma.views._
import org.denigma.frontend.tests.{LongListView, RandomView}
import scala.util.{Success, Failure, Try}
import scala.collection.immutable._
import org.scalajs.spickling.{PicklerRegistry => pr}
import org.scalajs.spickling.jsany._
import scala.scalajs.js
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.jquery.jQuery
import org.scalajs.dom.extensions.{AjaxException, Ajax}
import scala.concurrent.Promise
import org.denigma.extensions.sq
import org.scalajs.jquery.jQuery
import org.denigma.extensions._

@JSExport
object ScalaJavaScript extends OrdinaryView("main",dom.document.body)  with scalajs.js.JSApp
{
  rp.registerPicklers()

  val sidebarParams =  js.Dynamic.literal(exclusive = false)

  org.denigma.views
    .register("login", (el, params) =>Try(new LoginView(el,params)))
    .register("menu", (el, params) =>Try{ new MenuView(el,params) })
    .register("random",(el,params)=> Try {new RandomView(el,params)})
    .register("lists",(el,params)=>Try {new LongListView(el,params)})
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))


  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  @JSExport
  def main(): Unit = {

    jQuery(".top.sidebar").dyn.sidebar(sidebarParams).sidebar("show")

    val str = s"http://${dom.window.location.host}/test"

    //js.debugger()

    this.bind(this.element)
//    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("show")


    rp.registerPicklers()
  }

  val toggle: Var[MouseEvent] = Var(this.createMouseEvent())

//  toggle.handler {
//    //jQuery(".top.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
//
//    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
//  }


  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)
}
