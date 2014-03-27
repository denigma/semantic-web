package org.denigma.frontend

import scalatags.all._
import org.denigma.frontend.views._
import rx.core.Var
import scala.scalajs.js
import org.scalajs.jquery.JQueryXHR
import models.Message
import scala.util.Random
import scalatags._
import scala.scalajs.js.annotation.JSExport
import scala.collection.immutable.Map
import rx._
import models.Message
import scalatags.HtmlTag
import org.scalajs.dom.HTMLElement
import org.scalajs.dom

/**
 * Main class of scalajs app
 */
abstract class MainClass extends OrdinaryView("main",dom.document.body)
{


  val isSigned: Var[Boolean] = Var(false)


}
