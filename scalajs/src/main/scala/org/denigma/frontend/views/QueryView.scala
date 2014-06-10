package org.denigma.frontend.views

import rx._
import scala.collection.immutable._
import org.denigma.views._
import scalatags.Text.Tag
import org.scalajs.dom._
import org.denigma.views.core.OrdinaryView

/**
 * View for paper viewer
 */
class QueryView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("query",element)
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

}
