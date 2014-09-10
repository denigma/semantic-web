package org.denigma.frontend.views

import org.denigma.binding.views.BindableView
import org.denigma.controls.general.EditableMenuView
import org.denigma.semantic.models.PropertyModelView
import rx._
import org.scalajs.dom._
import scala.collection.immutable._
import scalatags.Text.Tag

/**
 * Menu view, this view is devoted to displaying menus
 * @param el html element
 * @param params view params (if any)
 */
class MenuView(el:HTMLElement, params:Map[String,Any] = Map.empty) extends EditableMenuView("menu",el,params)
{


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)
}