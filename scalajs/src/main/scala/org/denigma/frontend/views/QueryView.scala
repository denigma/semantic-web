package org.denigma.frontend.views

import rx._
import models._
import org.scalajs.dom
import org.scalajs.dom._
import scala.collection.immutable._
import org.denigma.views._
import org.denigma.extensions._
import models.Menu
import scala.util.Success
import scala.util.Failure
import scalatags.HtmlTag
import models.MenuItem
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalax.semweb.rdf.IRI
import org.scalajs.dom._
import org.scalajs.dom.extensions._

/**
 * View for paper viewer
 */
class QueryView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("query",element)
{

  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

}
