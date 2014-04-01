package org.denigma.views

import org.scalajs.dom

import scala.collection.mutable
import org.scalajs.dom.{Attr, HTMLElement}
import org.denigma.binding.{EventBinding, HtmlBinding, PropertyBinding}


/**
 * Just a class that can bind either properties of HTML rxes
 * @param name
 * @param elem
 */
abstract class OrdinaryView(name:String,elem:dom.HTMLElement) extends BindingView(name,elem)
  with PropertyBinding
  with HtmlBinding
  with EventBinding
{

  override def bindAttributes(el:HTMLElement,ats:mutable.Map[String, Attr]) = {
    this.bindHTML(el,ats)
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)

  }




}
