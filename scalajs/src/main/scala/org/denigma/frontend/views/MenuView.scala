package org.denigma.frontend.views

import rx._
import models.Menu
import models.WebIRI
import scalatags.HtmlTag
import models.MenuItem
import org.scalajs.dom
import org.scalajs.dom.{TextEvent, MouseEvent, Attr, HTMLElement}
import scala.collection.immutable._
import scala.collection.mutable
import org.denigma.views._
import dom.extensions._

/**
 * Menu view, this view is devoted to displaying menus
 * @param el html element
 * @param params view params (if any)
 */
class MenuView(el:HTMLElement, params:Map[String,Any] = Map.empty) extends ListView("menu",el,params)
{

  val testMenu: Var[Menu] = Var { Menu(WebIRI("http://webintelligence.eu"),"Home",
    MenuItem(WebIRI("http://webintelligence.eu/pages/about"),"About"),
    MenuItem(WebIRI("http://webintelligence.eu/pages/project"),"Project"),
    MenuItem(WebIRI("http://webintelligence.eu/another"),"Another")
  ) }

  val items: Rx[List[Map[String, Any]]] = Rx {
    val menu = testMenu()
    menu.children.map(ch=>Map[String,Any]("label"->ch.label,"uri"->ch.uri.stringValue))
  }

  override lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  override lazy val  lists: Map[String, Rx[scala.List[Map[String, Any]]]] = this.extractListRx(this)
}


