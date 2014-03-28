package org.denigma.extensions

import org.scalajs.dom
import org.scalajs.dom.{Attr, NodeList, Element, HTMLElement}

/**
 * Scala query for convenient DOM manipulation
 */
object sq{

  /**
   * Creates attribute
   * @param name
   * @param value
   * @return
   */
  def makeAttr(name:String,value:String): Attr = {
    val r = dom.document.createAttribute(name)
    r.value = value
    r
  }

  def byId(id:String): Option[HTMLElement] = dom.document.getElementById(id) match {
    case null=>None
    case el=>Some(el)
  }

  def find(query:String): Option[Element] = dom.document.querySelector(query) match
  {
    case null=>None
    case el=>Some(el)

  }

  def query(query:String): NodeList = dom.document.querySelectorAll(query)
}