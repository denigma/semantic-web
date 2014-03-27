package org.denigma.frontend.views

import org.scalajs.dom
import scala.collection.immutable.Map
import rx._
import scalatags.HtmlTag
import org.denigma.frontend.bindings.{PropertyBinding, HtmlBinding}
import scala.collection.mutable
import scala.scalajs.js
import org.scalajs.dom.HTMLElement
import scala.Predef


/**
 * Just a class that can bind either properties of HTML rxes
 * @param name
 * @param elem
 */
abstract class OrdinaryView(name:String,elem:dom.HTMLElement) extends BindingView(name,elem) with PropertyBinding with HtmlBinding
{

  override def bindAttributes(el:HTMLElement,ats:mutable.Map[String, js.String]) = {
    this.bindHTML(el,ats)
    this.bindProperties(el,ats)

  }


}
