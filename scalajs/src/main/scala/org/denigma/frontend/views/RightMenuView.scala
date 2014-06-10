package org.denigma.frontend.views

import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.denigma.views.core.OrdinaryView
import rx._
import scalatags._
import scalatags.Text.Tag

/**
 * View that displayes sparql quering interface
 * @param element
 * @param params
 */
class RightMenuView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("righ-menu",element)
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

}
