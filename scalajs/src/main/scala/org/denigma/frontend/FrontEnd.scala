package org.denigma.frontend

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{BindableView}
import org.denigma.binding.views.utils.ViewInjector
import org.denigma.frontend.views._
//import org.denigma.semantic.shapes.HeadersView
import org.denigma.frontend.views.datagrids._
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalajs.jquery.{jQuery => jq}
import rx._

import scala.collection.immutable._
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.util.Try
import scalatags.Text.Tag
import org.denigma.binding.extensions._

@JSExport
object FrontEnd extends BindableView  with scalajs.js.JSApp
{
  override val params:Map[String,Any] = Map.empty

  override val name = "main"
  lazy val elem:HTMLElement = dom.document.body

  val sidebarParams =  js.Dynamic.literal(
    exclusive = false,
    dimPage = false,
    closable =  false
  )
  /**
   * Register views
   */
  ViewInjector
    .register("login", (el, params) =>Try(new LoginView(el,params)))
    .register("menu", (el, params) =>Try{ new MenuView(el,params) })
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))
    .register("righ-menu", (el, params) =>Try(new RightMenuView(el,params)))
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("query", (el, params) =>Try(new QueryView(el,params)))
    .register("paper", (el, params) =>Try(new PaperView(el,params)))
    .register("page", (el, params) =>Try(new PageView(el,params)))

    .register("ReportsView",(el, params) =>Try(new ReportsView(el,params)))
    .register("report",(el, params)=>Try(new Report(el,params)))



  ViewInjector //register quering
    .register("query", (el, params) =>Try(new QueryView(el,params)))
    .register("query-headers", (el, params,parent) =>Try(new QueryHeaderView(el,params,parent)))

  //.register("sparql",(el, params) =>Try(new SelectQueryView(el,params)))

  ViewInjector //register datagrids
    .register("Headers",(el,params,parent)=>Try(new HeadersView(el,params,parent)))
    .register("Label",(el,params)=>Try(new LabelView(el,params)))
    .register("DataGrid",(el,params) => Try(new DataGrid(el,params)))
    .register("GridRow",(el,params) => Try(new GridRow(el,params)))
    .register("GridCell",(el,params) => Try(new GridCell(el,params)))


  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {this.binders = BindableView.defaultBinders(this)}


  @JSExport
  def main(): Unit = {

    rp.register()

    this.bindView(this.viewElement)
    jq(".top.sidebar").dyn.sidebar(sidebarParams).sidebar("show")
    jq(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("show")

  }

  @JSExport
  def load(content:String,into:String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

//  @JSExport
//  def moveInto(from:String,into:String): Unit = {
//
//    val ins: HTMLElement = dom.document.getElementById(from)
//    val into = dom.document.getElementById(into)
//    this.switchInner()
//    dom.document.getElementById(into).innerHTML =ins.innerHTML
//
//    ins.parentNode.removeChild(ins)
//  }

  val toggle: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

}
