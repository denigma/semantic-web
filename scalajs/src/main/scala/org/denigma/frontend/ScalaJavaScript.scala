package org.denigma.frontend

import org.scalajs.dom
import scalatags.all._
import scalatags.HtmlTag
import rx._
import scala.scalajs.js.annotation.JSExport
import org.denigma.extensions._
import models.{RegisterPicklers=>rp}

import org.scalajs.dom.{HTMLElement, MouseEvent, console}

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
import org.scalax.semweb.rdf.IRI

@JSExport
object ScalaJavaScript extends OrdinaryView("main",dom.document.body)  with scalajs.js.JSApp
{
  rp.registerPicklers()

  val sidebarParams =  js.Dynamic.literal(exclusive = false)

  /**
   * Register views
   */
  org.denigma.views
    .register("login", (el, params) =>Try(new LoginView(el,params)))
    .register("menu", (el, params) =>Try{ new MenuView(el,params) })
    .register("random",(el,params)=> Try {new RandomView(el,params)})
    .register("lists",(el,params)=>Try {new LongListView(el,params)})
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))
    .register("righ-menu", (el, params) =>Try(new RightMenuView(el,params)))
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("query", (el, params) =>Try(new QueryView(el,params)))
    .register("paper", (el, params) =>Try(new PaperView(el,params)))


  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  @JSExport
  def main(): Unit = {
    rp.registerPicklers()
    this.bind(this.viewElement)
    jQuery(".top.sidebar").dyn.sidebar(sidebarParams).sidebar("show")
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("show")

  }

  @JSExport
  def load(content:String,into:String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from:String,into:String): Unit = {
    val ins: HTMLElement = dom.document.getElementById(from)
    dom.document.getElementById(into).innerHTML =ins.innerHTML

    ins.parentNode.removeChild(ins)
  }

  val toggle: Var[MouseEvent] = Var(this.createMouseEvent())

}
