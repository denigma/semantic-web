package org.denigma.frontend.bindings

import org.denigma.macroses.js.ListRxMap
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.scalajs.js
import rx.Rx
import org.denigma.frontend.extensions._

/**
 * Trait that provides collection binding
 */
trait CollectionBinding extends PropertyBinding{

  def extractListRx[T: ListRxMap](t: T): Map[String, Rx[List[Map[String, Any]]]] =  implicitly[ListRxMap[T]].asListRxMap(t)

  def lists: Map[String, Rx[List[Map[String, Any]]]]

  def bindCollection(el:HTMLElement,ats:mutable.Map[String, js.String]) = if(el.style.display.toString!="none"){
//    el.style.display = "none"
//    for {
//      name<-ats.get("list")
//      col <-this.lists.get(name)
//    }{
//      val w = col.watcher
//      val list = col()
//      list.foreach{item=>
//      }
//    }

  }

//  def bindElement(el:HTMLElement, params:Map[String, Any],ats:mutable.Map[String, js.String]) = {
//    for {
//      (key,value)<-ats
//      p<-params.get(key)
//    }{
//      val v = value
//
//    }
//  }
//
//  def addItem(template:HTMLElement, params:Map[String, Any],ats:mutable.Map[String, js.String]) = {
//    val parent = template.parentElement
//    val node =
//
//
//  }

  def hide() = {

  }

}
