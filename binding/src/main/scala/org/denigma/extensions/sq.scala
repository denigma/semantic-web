package org.denigma.extensions

import scala.Some

import org.scalajs.dom._

import org.scalajs.dom.Attr
import org.scalajs.dom
import dom.extensions._
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js
import scala.concurrent.Future


import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
/**
 * "ScalaQuery" helper for convenient DOM manipulation and other useful things
 */
object sq{


  /** *
    * transforma string into address with current host
    * @param str
    * @return
    */
  def h(str:String): String = "http://"+ dom.window.location.host+( if(str.startsWith("/") ) "" else "/")+str

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
    case undef:js.Undefined=>None
    case el=>Some(el)
  }

  def find(query:String): Option[Element] = dom.document.querySelector(query) match
  {
    case null=>None
    case el=>Some(el)

  }

  def query(query:String): NodeList = dom.document.querySelectorAll(query)


  /**
   * Post method that does nice thing
   * @param url
   * @param data
   * @param timeout
   * @param headers
   * @param withCredentials
   * @return
   */
  def post(
                url:String,data:js.Any,timeout:Int = 0,
                headers: Seq[(String, String)] =("Content-Type", "application/json;charset=UTF-8")::Nil,
                withCredentials:Boolean = false
                ): Future[XMLHttpRequest] =
  {

    val jsonStr: String =     data match {
      case d:js.String=>d.toString
      case _=>g.JSON.stringify(data).toString
    }

    Ajax.apply("POST", url, jsonStr, timeout, headers, withCredentials)
  }
}