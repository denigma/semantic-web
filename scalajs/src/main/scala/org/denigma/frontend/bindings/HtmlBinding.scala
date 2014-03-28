package org.denigma.frontend.bindings

import rx._
import scalatags.HtmlTag
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.collection.immutable._
import scala.scalajs.js
import org.denigma.macroses.js.{TagRxMap, Binder}
import org.denigma.macroses
import org.denigma.frontend.extensions._

/**
 * HTML binding
 */
trait HtmlBinding {


  def tags:Map[String,Rx[HtmlTag]]

  //def extractTags[T]:Map[String,Rx[HtmlTag]] = macro Binder.htmlBindings_impl[T]

  def extractTagRx[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)



  def bindHTML(el:HTMLElement,ats:mutable.Map[String, dom.Attr]) =
    ats.get("html").flatMap(value=>this.tags.get(value.value).map(v=>(value.value,v))).foreach{case (key,rx)=>
      this.updateAttrByRx(key,el,rx)
    }

  def updateAttrByRx(key:String,el:org.scalajs.dom.HTMLElement ,rtag:Rx[HtmlTag]) = {
    val tg = rtag()
    tg.attrs.foreach {
      case (k, v) =>
        val atr = dom.document.createAttribute(k)
        atr.value = v
        el.attributes.setNamedItem(atr)
    }

    if(el.id === null) el.id = key
    val eid = el.id

    Obs(rtag) {
      val element = dom.document.getElementById(eid)
      element.innerHTML = rtag().toString
    }
  }
}
