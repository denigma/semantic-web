package org.denigma.frontend.views

import org.denigma.views.OrdinaryView
import org.scalajs.dom.{TextEvent, MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag
import scala.collection.immutable.Map

import org.scalajs.dom.MouseEvent
import org.scalajs.dom

/**
 * Login view
 */
class LoginView(element:HTMLElement) extends OrdinaryView("login",element)
{
  lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvens(this)


  val loginClick: Var[MouseEvent] = Var(this.createMouseEvent())

  val registerClick: Var[MouseEvent] = Var(this.createMouseEvent())


  val isSigned: Var[Boolean] = Var(false)




}
