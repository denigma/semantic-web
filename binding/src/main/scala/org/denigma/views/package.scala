package org.denigma

import scala.collection.immutable.Map
import org.scalajs.dom.{HTMLElement, Node}


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
