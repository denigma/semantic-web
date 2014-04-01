package org.denigma.frontend.views

import rx._
import models.Menu
import scalatags.all._
import models.WebIRI
import scalatags.HtmlTag
import models.MenuItem
import org.scalajs.dom
import org.scalajs.dom.{Attr, HTMLElement}
import scala.collection.immutable.Map
import scala.collection.mutable
import org.denigma.binding._
import org.denigma.views._

class MenuView(el:HTMLElement, params:Map[String,Any]) extends BindingView("menu",el) with PropertyBinding
{

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  val testMenu: Var[Menu] = Var { Menu(WebIRI("http://webintelligence.eu"),"Home",
    MenuItem(WebIRI("http://webintelligence.eu/pages/about"),"About"),
    MenuItem(WebIRI("http://webintelligence.eu/pages/project"),"Project"),
    MenuItem(WebIRI("http://webintelligence.eu/another"),"Another")
  ) }



  /** draws appropriate menu
   * @param m
   * @return
   */
  def menuItemTemplate(m:MenuItem): HtmlTag = {
    a(`class`:="item", href:=m.uri.stringValue,
      h4( `class`:="ui teal header",
        i(`class`:="home icon",
          m.label
        )
      )
    )

  }

  case class TestCase(hello:String,world:String)




//  val menu: Rx[HtmlTag] = Rx {
//    val items = this.testMenu().children.map(this.menuItemTemplate)
//    this.testMenu().children.map(this.menuItemTemplate)
//    div(items)
//  }
  override def bindAttributes(el: HTMLElement, ats: mutable.Map[String, Attr]): Unit = {
    dom.console.log("menus are not implemented yet")
  }
}


