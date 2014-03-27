package org.denigma.frontend

import scala.collection.immutable.Map
import org.scalajs.dom.{HTMLElement, Node}
import org.denigma.frontend.views.BindingView

/**
 * Implicits for views
 */
package object views {

  type ViewFactory = (HTMLElement,AnyRef*)=>BindingView


  var factories = Map.empty[String,ViewFactory]

  def register(name:String,factory:ViewFactory) = {
    this.factories = this.factories+(name->factory)
    this
  }



}
