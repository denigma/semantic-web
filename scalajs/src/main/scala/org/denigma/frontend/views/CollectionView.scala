package org.denigma.frontend.views

import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.scalajs.js
import org.scalajs.dom


/**
 * View for collections of items
 * @param name
 * @param el
 */
class CollectionView(name:String,el:HTMLElement) extends BindingView(name,el)
{
  override def bindAttributes(el: HTMLElement, ats: mutable.Map[String, js.String]): Unit = {

  }
}
